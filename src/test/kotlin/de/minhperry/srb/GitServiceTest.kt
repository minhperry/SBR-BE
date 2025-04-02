package de.minhperry.srb

import de.minhperry.srb.services.importer.GitService
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.PullCommand
import org.eclipse.jgit.api.PullResult
import org.eclipse.jgit.api.errors.InvalidRemoteException
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.io.TempDir
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.slf4j.LoggerFactory
import java.io.File
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

/**
 * A test class for the [de.minhperry.srb.services.importer.GitService] class.
 */
class GitServiceTest {
    private val logger = LoggerFactory.getLogger(this::class.java)

    @TempDir
    lateinit var tempDir: File

    private lateinit var gitService: GitService

    // Use a more lightweight repo just for the sake of testing
    private val repoUrl = "https://github.com/zpqrtbnk/test-repo"

    private lateinit var mockGit: Git

    @BeforeEach
    fun setUp() {
        gitService = GitService(tempDir.absolutePath)
        mockGit = mock(Git::class.java)
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
        val mockCloneCmd = mock(Git.cloneRepository()::class.java)

        `when`(mockCloneCmd.setURI(anyString())).thenReturn(mockCloneCmd)
        `when`(mockCloneCmd.setDirectory(any(File::class.java))).thenReturn(mockCloneCmd)
        `when`(mockCloneCmd.call()).thenReturn(mockGit)

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
        // mockStatic(Git.javaClass)
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

        val mockPullCmd = mock(PullCommand::class.java)
        val mockPullResult = mock(PullResult::class.java)

        `when`(mockGit.pull()).thenReturn(mockPullCmd)
        `when`(mockPullCmd.call()).thenReturn(mockPullResult)
        `when`(mockPullResult.isSuccessful).thenReturn(true)

        assertDoesNotThrow {
            gitService.pull()
        }
    }

    @Test
    fun `pull should throw if an exception occurs`() {
        tempDir.resolve(".git").mkdir()

        val mockPullCmd = mock(PullCommand::class.java)

        `when`(mockGit.pull()).thenReturn(mockPullCmd)
        `when`(mockPullCmd.call()).thenThrow(InvalidRemoteException("Invalid remote!"))

        assertFailsWith<InvalidRemoteException> {
            gitService.pull()
        }
    }
}
