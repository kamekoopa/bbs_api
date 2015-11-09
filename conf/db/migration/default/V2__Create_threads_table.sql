CREATE TABLE threads (
  id INT NOT NULL AUTO_INCREMENT,
  title VARCHAR(40) NOT NULL,
  created_by INT NOT NULL,
  created_at DATE NOT NULL,
  last_posted_at DATE DEFAULT NULL,
  FOREIGN KEY (created_by) REFERENCES users(id)
);
ALTER TABLE threads ADD CONSTRAINT threads_pk PRIMARY KEY (id);

CREATE TABLE tags (
  id int NOT NULL AUTO_INCREMENT,
  name VARCHAR(255) NOT NULL
);
ALTER TABLE tags ADD CONSTRAINT tags_pk PRIMARY KEY (id);
ALTER TABLE tags ADD CONSTRAINT tags_uq UNIQUE (name);

CREATE TABLE threads_tags (
  thread_id INT NOT NULL,
  tag_id INT NOT NULL,
  FOREIGN KEY (thread_id) REFERENCES threads(id),
  FOREIGN KEY (tag_id) REFERENCES tags(id)
);
ALTER TABLE threads_tags ADD CONSTRAINT threads_tags_pk PRIMARY KEY (thread_id, tag_id);
