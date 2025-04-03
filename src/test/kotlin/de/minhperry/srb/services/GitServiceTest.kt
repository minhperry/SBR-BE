package de.minhperry.srb.services

import de.minhperry.srb.services.importer.GitService
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import org.eclipse.jgit.api.CloneCommand
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.PullCommand
import org.eclipse.jgit.api.errors.InvalidRemoteException
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.io.TempDir
import org.slf4j.LoggerFactory
import java.io.File
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

/**
 * A test class for the [de.minhperry.srb.services.importer.GitService] class.
 */
@Deprecated("This test class is deprecated and should be removed in the future.")
private class GitServiceTest {
    private val logger = LoggerFactory.getLogger(this::class.java)

    @TempDir
    lateinit var tempDir: File

    private lateinit var gitService: GitService

    // Use a more lightweight repo just for the sake of testing
    private val repoUrl = "https://github.com/zpqrtbnk/test-repo"

    // Mocked objects
    private val mRepoDir = mockk<File>()
    private val mGit = mockk<Git>()
    private val mPullCmd = mockk<PullCommand>()
    private val mCloneCmd = mockk<CloneCommand>()

    @BeforeEach
    fun setUp() {
        gitService = GitService(tempDir.absolutePath)
    }

    @Test
    fun `clone should throw if directory exists`() {
        tempDir.resolve(".git").mkdir()

        assertFailsWith<IllegalStateException> {
            gitService.clone(repoUrl)
        }
    }

    @Test
    fun `clone should succeed if directory does not exist`() {
        every { mRepoDir.exists() } returns false
        mockkStatic(Git::class)

        every { Git.cloneRepository() } returns mCloneCmd
        every { mCloneCmd.setURI(any<String>()) } returns mCloneCmd
        every { mCloneCmd.setDirectory(any<File>()) } returns mCloneCmd
        every { mCloneCmd.call() } returns mGit

        assertDoesNotThrow {
            gitService.clone(repoUrl)
        }
    }

    @Test
    fun `clone should actually clone a repository`() {
        assertDoesNotThrow {
            gitService.clone(repoUrl)
        }

        assertTrue(tempDir.resolve(".git").exists()) // Ensure .git directory exists
    }

    @Test
    fun `clone should throw if an exception occurs`() {
        mockkStatic(Git::class)

        every { Git.cloneRepository() } returns mCloneCmd
        every { mCloneCmd.setURI(any<String>()) } returns mCloneCmd
        every { mCloneCmd.setDirectory(any<File>()) } returns mCloneCmd
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
    fun `pull should succeed if directory exists`() {
        tempDir.resolve(".git").mkdir()

        // ?

        assertDoesNotThrow {
            gitService.pull()
        }
    }

    @Test
    fun `pull should throw if an exception occurs`() {
        tempDir.resolve(".git").mkdir()

        // ?

        assertFailsWith<InvalidRemoteException> {
            gitService.pull()
        }
    }
}
