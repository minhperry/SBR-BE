package de.minhperry.srb.services.importer

import org.eclipse.jgit.api.Git
import org.slf4j.LoggerFactory
import java.io.File

/**
 * Service for handling Git repositories.
 * @param localPath Path to the local repository. Is relative to the project root.
 */
class GitService(
    private val localPath: String,
) {
    private val repoDir = File(localPath)
    private val logger = LoggerFactory.getLogger(this::class.java)

    /**
     * Clones a repository from a remote URL to the local path.
     * @param remote Remote URL of the repository.
     * @return true if the repository was cloned successfully, false otherwise.
     */
    fun clone(remote: String) {
        // If the repository already exists, do not clone it again
        if (repoDir.exists() && repoDir.list()?.isNotEmpty() == true) {
            throw IllegalStateException("Directory is not empty! Cannot clone!")
        }

        logger.info("Cloning repository from $remote to $localPath")

        Git
            .cloneRepository()
            .setURI(remote)
            .setDirectory(repoDir)
            .call()
            .use { logger.info("Cloning repository from $remote to $localPath") }
    }

    /**
     * Pulls the latest changes from the remote repository. Will always succeed since the repository is not modified
     * locally.
     * @return true if the repository was pulled successfully, false otherwise.
     */
    fun pull() {
        // If the repository does not exist, do not pull
        if (!repoDir.exists() || !File(repoDir, ".git").exists()) {
            throw IllegalStateException("Repository does not exist! Cannot pull!")
        }

        logger.info("Pulling latest changes from repository")
        Git.open(repoDir).use { git ->
            git.pull().call()
            logger.info("Pulling latest changes from repository successful!")
        }
    }

    /* Just leave here if this is deemed necessary in the future
     * Get the result of `git diff` command.
     * fun diff() {}
     */
}
