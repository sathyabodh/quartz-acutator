[[quartz]] = Quartz

Quartz actuator provides end points for managing jobs and triggers

[[quartz-jobs]] == Quartz Job (`quartz-jobs`)

The `quartz-jobs` endpoint provides access to application's quartz jobs

[[pause-job]] === Pause Job To pause particular job, make a `POST`
request to `/actuator/quartz-jobs/{group}/{name}/pause` as shown in the
following curl-based example:

include::{snippets}/quartz-jobs/named/job/pause/curl-request.adoc[]

[[resume-job]] === Resume Job To resume particular job, make a `POST`
request to `/actuator/quartz-jobs/{group}/{name}/resume` as shown in the
following curl-based example:

include::{snippets}/quartz-jobs/named/job/resume/curl-request.adoc[]

[[pause-jobgroup]] === Pause Job group To pause particular job group,
make `POST` request to `/actuator/quartz-jobs/{group}` as shown in the
following curl-based example:

include::{snippets}/quartz-jobs/named/jobgroup/pause/curl-request.adoc[]
