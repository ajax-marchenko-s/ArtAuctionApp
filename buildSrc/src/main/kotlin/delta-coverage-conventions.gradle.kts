import io.github.surpsg.deltacoverage.gradle.DeltaCoverageConfiguration

plugins {
    kotlin("jvm")
    id("io.github.surpsg.delta-coverage")
}

configure<DeltaCoverageConfiguration> {
    val targetBranch = project.properties["diffBase"]?.toString() ?: "refs/remotes/origin/master"
    diffSource.byGit {
        compareWith(targetBranch)
    }

    violationRules.failIfCoverageLessThan(0.6)
    reports {
        html = true
        markdown = true
    }
}
