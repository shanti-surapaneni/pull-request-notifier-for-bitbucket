# Pull Request Notifier for Bitbucket [![Build Status](https://travis-ci.org/tomasbjerre/pull-request-notifier-for-bitbucket.svg?branch=master)](https://travis-ci.org/tomasbjerre/pull-request-notifier-for-bitbucket)
The original use case was to trigger Jenkins jobs to build pull requests that are created in Bitbucket. The plugin can be configured to trigger different Jenkins jobs for different repositories. It can supply custom parameters to the jenkins job using the variables. It can authenticate with HTTP Basic.

It can, for example, trigger a build in Jenkins. Parameterized Jenkins jobs can be triggered remotely via:
```
http://server/job/theJob/buildWithParameters?token=TOKEN&PARAMETER=Value
```

The plugin can trigger any system, not only Jenkins. The plugin can notify any system that can be notified with a URL.

[Here](https://raw.githubusercontent.com/tomasbjerre/pull-request-notifier-for-bitbucket/master/sandbox/all.png) is a screenshot of the admin GUI.

[Here](http://bjurr.se/building-atlassian-bitbucket-pull-requests-in-jenkins/) is a blog post that includes the plugin.

Available in [Atlassian Marketplace](https://marketplace.atlassian.com/plugins/se.bjurr.prnfs.pull-request-notifier-for-stash).

## Features
The Pull Request Notifier for Bitbucket can:

* Invoke any URL, or set of URL:s, when a pull request event happens.
 * With variables available to add necessary parameters.
 * HTTP POST, PUT, GET and DELETE. POST and PUT also supports rendered post content. 
* Be configured to trigger on any [pull request event](https://developer.atlassian.com/static/javadoc/bitbucket/4.0.0/api/reference/com/atlassian/bitbucket/event/pull/package-summary.html). Including extended events:
 * RESCOPED_FROM, when source branch change
 * RESCOPED_TO, when target branch change
 * BUTTON_TRIGGER, when trigger button in pull request view is pressed
* Can invoke CSRF protected systems, using the ${INJECTION_URL_VALUE} variable. How to to that with Jenkins is described below.
* Be configured to only trigger if the pull request mathches a filter. A filter text is constructed with any combination of the variables and then a regexp is constructed to match that text.
* Add buttons to pull request view in Bitbucket. And map those buttons to URL invocations. This can be done by setting the filter string to ${BUTTON_TRIGGER_TITLE} and the filter regexp to title of button.
* Authenticate with HTTP basic authentication.
* Send custom HTTP headers
* Can optionally use proxy to connect
* Can let users and/or admins do configuration. Or restrict configuration to just system admins. A user will have to browse to the configuration page at `http://domain/bitbucket/plugins/servlet/prnfb/admin`.
* Can enable trigger
 * If PR has, or has no, conflicts
 * Only if PR has conflicts
 * Only if PR has no conflicts

The plugin has its own implementation to create the RESCOPED_FROM and RESCOPED_TO events. RESCOPED is transformed to RESCOPED_TO if target branch changed, RESCOPED_FROM if source branch, or both, changed.

The filter text as well as the URL support variables. These are:

* ${PULL_REQUEST_ID} Example: 1
* ${PULL_REQUEST_TITLE} Example: Anything
* ${PULL_REQUEST_VERSION} Example: 1
* ${PULL_REQUEST_COMMENT_TEXT} Example: A comment
* ${PULL_REQUEST_ACTION} Example: OPENED
* ${BUTTON_TRIGGER_TITLE} Example: Trigger Notification
* ${INJECTION_URL_VALUE} Value retrieved from any URL
* ${PULL_REQUEST_URL} Example: http://localhost:7990/projects/PROJECT_1/repos/rep_1/pull-requests/1
* ${PULL_REQUEST_USER_DISPLAY_NAME} Example: Some User
* ${PULL_REQUEST_USER_EMAIL_ADDRESS} Example: some.user@bitbucket.domain
* ${PULL_REQUEST_USER_ID} Example: 1
* ${PULL_REQUEST_USER_NAME} Example: user.name
* ${PULL_REQUEST_USER_SLUG} Example: user.name
* ${PULL_REQUEST_AUTHOR_DISPLAY_NAME} Example: Administrator
* ${PULL_REQUEST_AUTHOR_EMAIL} Example: admin@example.com
* ${PULL_REQUEST_AUTHOR_ID} Example: 1
* ${PULL_REQUEST_AUTHOR_NAME} Example: admin
* ${PULL_REQUEST_AUTHOR_SLUG} Example: admin
* ${PULL_REQUEST_FROM_SSH_CLONE_URL} Example: ssh://git@localhost:7999/project_1/rep_1
* ${PULL_REQUEST_FROM_HTTP_CLONE_URL} Example: http://admin@localhost:7990/bitbucket/scm/project_1/rep_1.git
* ${PULL_REQUEST_FROM_HASH} Example: 6053a1eaa1c009dd11092d09a72f3c41af1b59ad
* ${PULL_REQUEST_FROM_ID} Example: refs/heads/branchmodmerge
* ${PULL_REQUEST_FROM_BRANCH} Example: branchmodmerge
* ${PULL_REQUEST_FROM_REPO_ID} Example: 1
* ${PULL_REQUEST_FROM_REPO_NAME} Example: rep_1
* ${PULL_REQUEST_FROM_REPO_PROJECT_ID} Example: 1
* ${PULL_REQUEST_FROM_REPO_PROJECT_KEY} Example: PROJECT_1
* ${PULL_REQUEST_FROM_REPO_SLUG} Example: rep_1
* And same variables for TO, like: ${PULL_REQUEST_TO_HASH}

The ${PULL_REQUEST_USER...} contains information about the user who issued the event. Who commented it, who rejected it, who approved it...

You may want to use [Violation Comments to Stash plugin](https://wiki.jenkins-ci.org/display/JENKINS/Violation+Comments+to+Stash+Plugin) and/or [StashNotifier plugin](https://wiki.jenkins-ci.org/display/JENKINS/StashNotifier+Plugin) to report results back to Bitbucket.

### Jenkins
Parameterized Jenkins jobs can be triggered remotely via:
```
http://server/job/theJob/buildWithParameters?token=TOKEN&PARAMETER=Value
```

If you are using a CSRF protection in Jenkins, you can use the **Injection URL** feature.
* Set **Injection URL** field to `http://JENKINS/crumbIssuer/api/xml?xpath=//crumb/text()`. You may get an error like *primitive XPath result sets forbidden; implement jenkins.security.SecureRequester*. If so, you can set Injection URL to `http://JENKINS/crumbIssuer/api/xml?xpath=//crumb` in combination with regular expression `<crumb>([^<]*)</crumb>`. Or a third option is to checkout [this](https://wiki.jenkins-ci.org/display/JENKINS/Secure+Requester+Whitelist+Plugin) Jenkins plugin.
* In the headers section, set header **.crumb** with value **${INJECTION_URL_VALUE}**.

## Developer instructions
Prerequisites:

* Atlas SDK [(installation instructions)](https://developer.atlassian.com/docs/getting-started/set-up-the-atlassian-plugin-sdk-and-build-a-project).
* JDK 1.7 or newer

Generate Eclipse project:
```
atlas-compile eclipse:eclipse
```

Package the plugin:
```
atlas-package
```

Run Bitbucket, with the plugin, on localhost:
```
export MAVEN_OPTS=-Dplugin.resource.directories=`pwd`/src/main/resources
mvn bitbucket:run
```

You can also debug with:
```
mvn bitbucket:debug
```

Make a release [(detailed instructions)](https://developer.atlassian.com/docs/common-coding-tasks/development-cycle/packaging-and-releasing-your-plugin):
```
mvn release:prepare
mvn release:perform
```
