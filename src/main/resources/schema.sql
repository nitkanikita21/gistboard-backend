CREATE TABLE IF NOT EXISTS users (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       username VARCHAR(50) UNIQUE NOT NULL,
                       email VARCHAR(255) UNIQUE NOT NULL,
                       role VARCHAR(50) NOT NULL,
                       avatar VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS notes (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       name VARCHAR(255),
                       content TEXT,
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                       views BIGINT DEFAULT 0
);

INSERT INTO notes (name, content) VALUES ('Note 1', '# Title 1\n\nThis is the **content** of the first note.\n\n- Item 1\n- Item 2\n- Item 3');
INSERT INTO notes (name, content) VALUES ('Note 2', '# Title 2\n\nThis is the *content* of the second note.\n\n1. First item\n2. Second item\n3. Third item');
INSERT INTO notes (name, content) VALUES ('Note 3', '# Title 3\n\nThis note contains `inline code`.\n\n- Task 1\n- Task 2\n- Task 3');
INSERT INTO notes (name, content) VALUES ('Note 4', '# Title 4\n\n> This is a quote from a famous person.\n\nAnd some additional text.');
INSERT INTO notes (name, content) VALUES ('Note 5', '# Title 5\n\nA **bold** statement here and a [link](https://example.com) there.');
INSERT INTO notes (name, content) VALUES ('Note 6', '# Title 6\n\nHere is a sample table:\n\n| Header 1 | Header 2 |\n|----------|----------|\n| Cell 1   | Cell 2   |\n| Cell 3   | Cell 4   |');
INSERT INTO notes (name, content) VALUES ('Note 7', '# Title 7\n\nSome `inline code` and a code block:\n\n```\ndef hello():\n    print("Hello, world!")\n```');
INSERT INTO notes (name, content) VALUES ('Note 8', '# Title 8\n\n**Important**: Don''t forget to check this note.\n\n### Subheading\n\nMore detailed content here.');
INSERT INTO notes (name, content) VALUES ('Note 9', '# Title 9\n\n## Subheading\n\nThis is the content of note 9.\n\n- Step 1\n- Step 2\n- Step 3');
INSERT INTO notes (name, content) VALUES ('Note 10', '# Title 10\n\nMarkdown supports images too:\n\n![Image](https://via.placeholder.com/150)');
INSERT INTO notes (name, content) VALUES ('Note 11', '# Title 11\n\n**Highlights:** Important details about the project.\n\n1. Phase 1\n2. Phase 2\n3. Phase 3');
INSERT INTO notes (name, content) VALUES ('Note 12', '# Title 12\n\n> A quote can add emphasis to content.\n\nMake sure to review this.');
INSERT INTO notes (name, content) VALUES ('Note 13', '# Title 13\n\n- Bullet point\n- Another bullet\n\nThis is note 13.');
INSERT INTO notes (name, content) VALUES ('Note 14', '# Title 14\n\nAdding a checklist:\n\n- [ ] Task 1\n- [x] Completed Task');
INSERT INTO notes (name, content) VALUES ('Note 15', '# Title 15\n\nThis note contains **bold** and *italic* text for emphasis.');
INSERT INTO notes (name, content) VALUES ('Note 16', '# Title 16\n\nSome code snippet:\n\n```python\nprint("Hello, World")\n```');
INSERT INTO notes (name, content) VALUES ('Note 17', '# Title 17\n\nExploring lists:\n\n1. First\n2. Second\n3. Third');
INSERT INTO notes (name, content) VALUES ('Note 18', '# Title 18\n\nIncluding links:\n\n[Click here](https://example.com)');
INSERT INTO notes (name, content) VALUES ('Note 19', '# Title 19\n\nMarkdown tables:\n\n| A | B |\n|---|---|\n| 1 | 2 |');
INSERT INTO notes (name, content) VALUES ('Note 20', '# Title 20\n\nThis is an example of nested lists:\n\n- Level 1\n  - Level 2');