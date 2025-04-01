package de.minhperry.srb.services

import org.eclipse.jgit.api.Git
import org.slf4j.LoggerFactory
import java.io.File

class ImportService(private val localPath: String) {
    private val repoDir = File(localPath)
    private val logger = LoggerFactory.getLogger(this::class.java)

    fun clone(remote: String) {
        if (repoDir.exists() && repoDir.list()?.isNotEmpty() == true) {
            logger.info("Repository already exists! Cannot clone!")
            return
        }

        logger.info("Cloning repository from $remote to $localPath")
        try {
            Git.cloneRepository()
                .setURI(remote)
                .setDirectory(repoDir)
                .call()
        } catch (e: Exception) {
            logger.error("Error while cloning repository: ", e)
            return
        }
        logger.info("Repository cloned successfully!")
    }
}