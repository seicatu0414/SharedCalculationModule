package org.shared_calculation_module.project

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform