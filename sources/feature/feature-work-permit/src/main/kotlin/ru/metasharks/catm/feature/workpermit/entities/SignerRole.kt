package ru.metasharks.catm.feature.workpermit.entities

enum class SignerRole(val code: String) {
    PERMIT_ISSUER("permit_issuer"),
    PERMITTER("permitter"),
    RESPONSIBLE_MANAGER_EXECUTOR("responsible_manager_executor"),
    INDUSTRIAL_SAFETY_SPECIALIST("industrial_safety_specialist"),
    LABOR_PROTECTION_SPECIALIST("labor_protection_specialist"),
    PERMIT_APPROVER("permit_approver"),
    PERMIT_ACCEPTOR("permit_acceptor"),
    RESPONSIBLE_MANAGER("responsible_manager"),
}
