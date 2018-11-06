package ca.rightsomegoodgames.mayacharm.run

import ca.rightsomegoodgames.mayacharm.mayacomms.MayaCommandInterface
import ca.rightsomegoodgames.mayacharm.settings.ProjectSettings
import com.intellij.execution.process.ProcessHandler
import com.intellij.execution.process.ProcessInfo
import com.intellij.execution.ui.ConsoleViewContentType
import com.intellij.execution.ui.ExecutionConsole
import com.intellij.openapi.project.Project
import com.intellij.xdebugger.XDebugSession
import com.jetbrains.python.debugger.PyDebugProcess
import java.net.ServerSocket

class MayaCharmDebugProcess(session: XDebugSession,
                            serverSocket: ServerSocket,
                            executionConsole: ExecutionConsole,
                            processHandler: ProcessHandler?,
                            multiProcess: Boolean,
                            private val proj: Project,
                            private val runConfig: MayaCharmRunConfiguration,
                            private val process: ProcessInfo
                            )
                            : PyDebugProcess(session,
                                serverSocket,
                                executionConsole,
                                processHandler,
                                multiProcess) {

    override fun printToConsole(text: String?, contentType: ConsoleViewContentType?) {
    }

    override fun detachDebuggedProcess() {
        handleStop()
    }

    override fun getConnectionMessage(): String {
        return "Attaching to a process with a PID=${process.pid}"
    }

    override fun getConnectionTitle(): String {
        return "Attaching Debugger"
    }

    override fun afterConnect() {
        super.afterConnect()

        val projectSettings = ProjectSettings.getInstance(proj)
        val maya = MayaCommandInterface(projectSettings.host, projectSettings.port)

        when (runConfig.executionType) {
            ExecutionType.FILE -> maya.sendFileToMaya(runConfig.scriptFilePath)
            ExecutionType.CODE -> maya.sendCodeToMaya(runConfig.scriptCodeText)
        }
    }
}
