package com.webcontrol.android.ui.newchecklist.data

enum class ScopesChecklist {
    CONSULT_INSPECTION,
    HISTORY,
    SWORN_DECLARATION,
    SIGNATURE,
    INPUT_DATA,
    LIST_CHECKLIST,
    CHECKLIST_FILLING,
    EVIDENCE
}

object NewCheckListScope {
    var scope = ScopesChecklist.HISTORY
}