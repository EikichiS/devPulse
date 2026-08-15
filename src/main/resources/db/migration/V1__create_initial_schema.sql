  CREATE TABLE app_users (
      git_hub_id    BIGINT        PRIMARY KEY,
      username      VARCHAR(255)  NOT NULL,
      avatar_url    VARCHAR(500),
      access_token  TEXT,
      created_at    TIMESTAMP     NOT NULL
  );

  CREATE TABLE repositories (
      git_repo_id BIGINT PRIMARY KEY,
      app_user_id BIGINT NOT NULL REFERENCES app_users(git_hub_id),
      name VARCHAR(255) NOT NULL, full_name VARCHAR(500) NOT NULL,
      primary_language VARCHAR(100),
      is_private BOOLEAN NOT NULL DEFAULT FALSE,
      last_synced_at TIMESTAMP
  );

  CREATE TABLE analysis_jobs (
      id VARCHAR(36) PRIMARY KEY,
      app_user_id BIGINT NOT NULL REFERENCES app_users(git_hub_id),
      status VARCHAR(20) NOT NULL,
      started_at TIMESTAMP,
      finished_at TIMESTAMP,
      error_message TEXT
  );

  CREATE TABLE repository_analyses (
      id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
      repository_id BIGINT NOT NULL REFERENCES repositories(git_repo_id),
      analysis_job_id VARCHAR(36) NOT NULL REFERENCES analysis_jobs(id),
      language_breakdown TEXT,
      detected_technologies TEXT,
      has_ci BOOLEAN NOT NULL DEFAULT FALSE,
      is_dockerized BOOLEAN NOT NULL DEFAULT FALSE,
      has_tests BOOLEAN NOT NULL DEFAULT FALSE
  );

  CREATE TABLE developer_profiles (
      id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
      app_user_id BIGINT NOT NULL UNIQUE REFERENCES app_users(git_hub_id),
      language_breakdown TEXT, total_repos_analyzed INT NOT NULL DEFAULT 0,
      activity_score INT NOT NULL DEFAULT 0,
      testing_score INT NOT NULL DEFAULT 0,
      ci_score INT NOT NULL DEFAULT 0,
      docs_score INT NOT NULL DEFAULT 0
    );