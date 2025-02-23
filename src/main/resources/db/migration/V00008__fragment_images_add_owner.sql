ALTER TABLE fragment_images
ADD owner BIGINT;

ALTER TABLE fragment_images
ADD CONSTRAINT fk_fragment_images_owner
FOREIGN KEY (owner) REFERENCES users(id);