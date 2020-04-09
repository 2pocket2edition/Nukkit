String getDiscordMessage() {
    def msg = "**Status:** " + currentBuild.currentResult.toLowerCase() + "\n**Branch:** ${BRANCH_NAME}\n**Changes:**\n"
    if (!currentBuild.changeSets.isEmpty()) {
        currentBuild.changeSets.first().getLogs().each {
            msg += "- `" + it.getCommitId().substring(0, 8) + "` *" + it.getComment().substring(0, Math.min(64, it.getComment().length() - 1)) + (it.getComment().length() - 1 > 64 ? "..." : "") + "*\n"
        }
    } else {
        msg += "- no changes\n"
    }

    msg += "\n**Artifacts:**\n"
    currentBuild.rawBuild.getArtifacts().each {
        msg += "- [" + it.getDisplayPath() + "](" + env.BUILD_URL + "artifact/" + it.getHref() + ")\n"
    }

    return msg.length() > 2048 ? msg.substring(0, 2045) + "..." : msg
}

pipeline {
    agent any
    tools {
        maven 'Maven 3'
        jdk 'Java 8'
    }
    options {
        buildDiscarder(logRotator(artifactNumToKeepStr: '5'))
    }
    stages {
        stage ('Build') {
            steps {
                sh 'mvn clean package'
            }
            post {
                success {
                    junit 'target/surefire-reports/**/*.xml'
                    archiveArtifacts artifacts: 'target/nukkit-*-SNAPSHOT.jar', fingerprint: true
                }
            }
        }
    }

    post {
        always {
            deleteDir()

            withCredentials([string(credentialsId: "2p2e_discord_webhook", variable: "discordWebhook")]) {
                discordSend thumbnail: "https://www.2p2e.net/logo.png",
                        result: currentBuild.currentResult,
                        description: getDiscordMessage(),
                        link: env.BUILD_URL,
                        title: "NukkitX:${BRANCH_NAME} #${BUILD_NUMBER}",
                        webhookURL: "${discordWebhook}"
            }
        }
    }
}