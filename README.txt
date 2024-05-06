to run: simply clone the repository and run the program in intellij
--------
Database Instructions:
1.	Open File explorer and navigate to the Windows (C:) folder.
2.	Create a folder (within the Windows (C:) folder) named SQlite.
3.	Drag or copy/paste the scrumptious.db file into the SQlite folder.

Folder and file names are case-sensitive: the path should match the latter half of the url stored in the java
program: C://SQLite/scrumptious.db

This should allow the java program and SQL library to access and manipulate the database that is now stored locally on your Windows PC.

Important: for testing, use the main method in the ‘Database’ class to clear your local database. This will delete all tuples from the
‘users’ and ‘schedules’ tables.
-------
requirements we didn't include:
1. didn't highlight courses required by account major
2. didn't provide a log of added and removed courses
3. didn't suggest courses that don't conflict with the schedule when a user adds a course that conflicts
4. didn't highlight courses that belong to the account major, didn't highlight courses base don major
5. didn't allow users to bookmark certain courses instead of adding them
6. didn't provide a special indicator in search results to show courses that can't be added (this wasn't really a requirement)