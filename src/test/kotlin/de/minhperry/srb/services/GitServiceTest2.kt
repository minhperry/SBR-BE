package de.minhperry.srb.services

import de.minhperry.srb.services.importer.GitService
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.verify
import org.eclipse.jgit.api.CloneCommand
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.PullCommand
import org.eclipse.jgit.api.PullResult
import org.eclipse.jgit.api.errors.InvalidRemoteException
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.io.TempDir
import java.io.File
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class GitServiceTest2 {
    @TempDir
    lateinit var repoDir: File

    private lateinit var gitService: GitService

    // Use a more lightweight repo just for the sake of testing
    private val repoUrl = "https://github.com/zpqrtbnk/test-repo"

    @BeforeEach
    fun setUp() {
        gitService = GitService(repoDir.absolutePath)
    }

    @Test
    fun `clone should throw if directory exists`() {
        repoDir.resolve(".git").mkdir()

        assertFailsWith<IllegalStateException> {
            gitService.clone(repoUrl)
        }
    }

    @Test
    fun `clone should succeed if directory is empty`() {
        val mCloneCmd = mockk<CloneCommand>()

        mockkStatic(Git::class)
        every { Git.cloneRepository() } returns mCloneCmd
        every { mCloneCmd.setURI(repoUrl) } returns mCloneCmd
        every { mCloneCmd.setDirectory(repoDir) } returns mCloneCmd
        every { mCloneCmd.call() } returns mockk(relaxed = true)

        assertDoesNotThrow {
            gitService.clone(repoUrl)
        }
    }

    @Test
    fun `clone should actually clone the repo`() {
        assertDoesNotThrow {
            gitService.clone(repoUrl)
        }

        // Assert that the directory is a git repository
        assertTrue(repoDir.exists())
        assertTrue(repoDir.resolve(".git").exists())
        assertEquals(7, repoDir.list()!!.size)

        // Assert the contents
        val files = repoDir.listFiles()!!
        val fileNames = files.map { it.name }
        assertContains(fileNames, "test.js")
        assertContains(fileNames, "hello.txt")
        assertContains(fileNames, "wtf.jpg")
    }

    @Test
    fun `clone should throw if an exception occurs`() {
        val mCloneCmd = mockk<CloneCommand>()

        mockkStatic(Git::class)
        every { Git.cloneRepository() } returns mCloneCmd
        every { mCloneCmd.setURI(repoUrl) } returns mCloneCmd
        every { mCloneCmd.setDirectory(repoDir) } returns mCloneCmd
        every { mCloneCmd.call() } throws InvalidRemoteException("Invalid remote!")

        assertFailsWith<InvalidRemoteException> {
            gitService.clone(repoUrl)
        }
    }

    @Test
    fun `pull should fail if directory does not exist`() {
        assertFailsWith<IllegalStateException> {
            gitService.pull()
        }
    }

    @Test
    fun `pull should succeed if it's a git repository`() {
        repoDir.resolve(".git").mkdir()

        val mGit = mockk<Git>()
        val mPullCmd = mockk<PullCommand>()
        val mPullRes = mockk<PullResult>()

        every { Git.open(repoDir) } returns mGit
        every { mGit.pull() } returns mPullCmd
        every { mPullCmd.call() } returns mPullRes
        every { mPullRes.isSuccessful } returns true

        gitService.pull()

        verify {
            mGit.pull()
            mPullCmd.call()
        }
    }
}
