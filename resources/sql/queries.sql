--name:get-contacts
--selects all contacts
SELECT * FROM contact

--name:create-contact!
--creates a new contact
INSERT INTO contact
(name, email, phone)
VALUES (:name, :email, :phone)
