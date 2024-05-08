CREATE TABLE users (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    username VARCHAR(40) NOT NULL,
    email VARCHAR(255) NOT NULL,
    password VARCHAR(40) NOT NULL,
    registration_date TIMESTAMP WITH TIME ZONE NOT NULL,
    profile_photo_url VARCHAR(2000),
    profile_description TEXT
);

CREATE TABLE posts (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    author_id BIGINT NOT NULL REFERENCES users(id),
    description TEXT,
    post_date TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE TABLE comments (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    author_id BIGINT NOT NULL REFERENCES users(id),
    post_id BIGINT NOT NULL REFERENCES posts(id),
    text TEXT,
    creation_date TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE TABLE likes (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id),
    post_id BIGINT NOT NULL REFERENCES posts(id)
);

CREATE TABLE image_storage (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    original_name VARCHAR(255), -- максимально допустимая длина имени файла в большинстве файловых систем
    file_path VARCHAR(1024), -- максимально допустимая длина пути на macos (на linux и win больше)
    post_id BIGINT NOT NULL REFERENCES posts(id)
);
