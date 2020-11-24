package org.springframework.jenkins.cloud.e2e

import groovy.transform.CompileStatic
import javaposse.jobdsl.dsl.DslFactory

import org.springframework.jenkins.cloud.common.AllCloudJobs

/**
 * @author Marcin Grzejszczak
 */
@CompileStatic
class BreweryEndToEndBuildMaker extends EndToEndBuildMaker {
	private final String repoName = 'brewery'

	BreweryEndToEndBuildMaker(DslFactory dsl) {
		super(dsl, 'spring-cloud-samples')
	}

	void build(String releaseTrainName) {
		this.build(releaseTrainName, releaseTrainName)
	}

	void build(String prefix, String releaseTrainName) {
		buildWithSwitches(prefix, defaultSwitches(releaseTrainName))
	}

	protected void buildWithSwitches(String prefix, String defaultSwitches) {
		super.build("$prefix-sleuth", repoName(), "runAcceptanceTests.sh -t SLEUTH $defaultSwitches", oncePerDay())
		super.build("$prefix-eureka", repoName(), "runAcceptanceTests.sh -t EUREKA $defaultSwitches", oncePerDay())
		super.build("$prefix-consul", repoName(), "runAcceptanceTests.sh -t CONSUL $defaultSwitches", oncePerDay())
		super.build("$prefix-zookeeper", repoName(), "runAcceptanceTests.sh -t ZOOKEEPER $defaultSwitches", oncePerDay())
		super.build("$prefix-wavefront", repoName(), "runAcceptanceTests.sh -t WAVEFRONT $defaultSwitches", oncePerDay())
	}

	protected String repoName() {
		return this.repoName
	}

	protected String defaultSwitches(String releaseTrainName) {
		String boot = AllCloudJobs.RELEASE_TRAIN_TO_BOOT_VERSION_MINOR.get(releaseTrainName.toLowerCase())
		String releaseTrain = releaseTrainName.capitalize()
		String bootVersion = boot.split("\\.").length == 3 ? boot : "\$( bootVersion \"${boot}\" )"
		String additionalSwitches = "--killattheend -v \"\$( springCloudVersion \"${releaseTrain}\" )\" --branch ${branchName()} -r -b \"${bootVersion}\""
		println "Found additional switches [${additionalSwitches}]"
		return additionalSwitches
	}
}
