package com.webcontrol.angloamerican.ui.approvemovements.ui.approveruser

sealed class ApproverUserState {
    class Success(val isApproverUser: Boolean): ApproverUserState()
    class Failure(val message: String): ApproverUserState()
}