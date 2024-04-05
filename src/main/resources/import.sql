INSERT INTO authors (name, created_at, enabled) VALUES('Author 1',NOW(),true);
INSERT INTO authors (name, created_at, enabled) VALUES('Author 2',NOW(),true);
INSERT INTO authors (name, created_at, enabled) VALUES('Author 3',NOW(),true);
INSERT INTO authors (name, created_at, enabled) VALUES('Author 4',NOW(),true);
INSERT INTO authors (name, created_at, enabled) VALUES('Author 5',NOW(),true);

INSERT INTO publishers (name, created_at, enabled) VALUES ('Publisher 1',NOW(),true);
INSERT INTO publishers (name, created_at, enabled) VALUES ('Publisher 2',NOW(),true);
INSERT INTO publishers (name, created_at, enabled) VALUES ('Publisher 3',NOW(),true);
INSERT INTO publishers (name, created_at, enabled) VALUES ('Publisher 4',NOW(),true);
INSERT INTO publishers (name, created_at, enabled) VALUES ('Publisher 5',NOW(),true);

INSERT INTO books (isbn, title, description, date_publication, amount_pages, amount_copies, amount_copies_borrowed, amount_copies_remaining, created_at, enabled, id_author, id_publisher) VALUES ('9780141439846', 'Book 1', 'This is the description for the book 1', '1851-10-18', 624, 100, 20, 80, NOW(), true, 1, 1);
INSERT INTO books (isbn, title, description, date_publication, amount_pages, amount_copies, amount_copies_borrowed, amount_copies_remaining, created_at, enabled, id_author, id_publisher) VALUES ('9780061120084', 'Book 2', 'This is the description for the book 2', '1960-07-11', 336, 150, 30, 120, NOW(), true, 2, 2);
INSERT INTO books (isbn, title, description, date_publication, amount_pages, amount_copies, amount_copies_borrowed, amount_copies_remaining, created_at, enabled, id_author, id_publisher) VALUES ('9780743273565', 'Book 3', 'This is the description for the book 3', '1925-04-10', 180, 200, 50, 150, NOW(), true, 3, 3);
INSERT INTO books (isbn, title, description, date_publication, amount_pages, amount_copies, amount_copies_borrowed, amount_copies_remaining, created_at, enabled, id_author, id_publisher) VALUES ('9780451524935', 'Book 4', 'This is the description for the book 4', '1949-06-08', 328, 120, 10, 110, NOW(), true, 4, 4);
INSERT INTO books (isbn, title, description, date_publication, amount_pages, amount_copies, amount_copies_borrowed, amount_copies_remaining, created_at, enabled, id_author, id_publisher) VALUES ('9780140449297', 'Book 5', 'This is the description for the book 5', '1605-01-16', 1056, 80, 5, 75, NOW(), true, 5, 5);

INSERT INTO users (name, last_name, phone, email, password, enabled, created_at) VALUES ("Lucas", "Aramberry", "9999999999", "lucasaramberry@admin.com", "$2a$10$ZeyDp9lBQ5Awxsn.rgQN6uJVocfje4wgYhHzT0Pcd6oGyyoC3Pqyu", true, "2024-03-03 16:59:36")
INSERT INTO users (name, last_name, phone, email, password, enabled, created_at) VALUES ("Lucas", "Aramberry", "7777777777", "lucasaramberry@user.com", "$2a$10$ZeyDp9lBQ5Awxsn.rgQN6uJVocfje4wgYhHzT0Pcd6oGyyoC3Pqyu", true, "2024-03-03 16:59:36")

INSERT INTO roles (id, name) VALUES (1, "ROLE_ADMIN");
INSERT INTO roles (id, name) VALUES (2, "ROLE_USER");

INSERT INTO users_roles (role_id, user_id) VALUES (1, 1);
INSERT INTO users_roles (role_id, user_id) VALUES (2, 1);
INSERT INTO users_roles (role_id, user_id) VALUES (2, 2);

INSERT INTO loans (date_loan, date_devolution, enabled, id_book, id_user) VALUES ("2024-04-01", "2024-12-31", true, 1, 2);
INSERT INTO loans (date_loan, date_devolution, enabled, id_book, id_user) VALUES ("2024-04-01", "2024-12-31", true, 2, 2);
INSERT INTO loans (date_loan, date_devolution, enabled, id_book, id_user) VALUES ("2024-04-01", "2024-12-31", true, 3, 2);