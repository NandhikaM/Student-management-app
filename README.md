# Student Management App — CI/CD Pipeline (Git + Maven + Docker + Jenkins)

A minimal Spring Boot web app with two operations (add + view students), built to
demonstrate a full CI/CD pipeline.

## What's in this project
```
student-mgmt/
├── pom.xml
├── Dockerfile
├── Jenkinsfile
├── .gitignore
└── src/
    ├── main/java/com/example/studentmgmt/
    │   ├── StudentMgmtApplication.java
    │   ├── model/Student.java
    │   ├── service/StudentService.java
    │   └── controller/StudentController.java
    ├── main/resources/application.properties
    └── test/java/com/example/studentmgmt/service/StudentServiceTest.java
```

Endpoints:
- `POST /students?name=John&department=CSE` → add a student
- `GET /students` → view all students
- `GET /students/health` → health check (used by the pipeline)

---

## 1. Git — set up and push the repo

```bash
cd student-mgmt
git init
git add .
git commit -m "Initial commit: Student Management app skeleton"

# make a few more meaningful commits as you go, e.g.:
git add src/main/java/com/example/studentmgmt/controller/StudentController.java
git commit -m "Add REST endpoints for add/view students"
git add src/test/java/...StudentServiceTest.java
git commit -m "Add unit tests for StudentService"
git add Dockerfile
git commit -m "Add Dockerfile for containerization"
git add Jenkinsfile
git commit -m "Add Jenkins pipeline definition"

# create an empty repo on GitHub first, then:
git branch -M main
git remote add origin https://github.com/<your-username>/student-mgmt.git
git push -u origin main
```
📸 **Screenshot**: your GitHub repo page showing the commit history (`git log --oneline` also works as a screenshot).

---

## 2. Maven — build, test, package

```bash
mvn clean package
```
This compiles the app, runs `StudentServiceTest`, and produces `target/student-mgmt.jar`.

To run it locally without Docker (optional sanity check):
```bash
java -jar target/student-mgmt.jar
# then in another terminal:
curl "http://localhost:8080/students?name=Nandhika&department=CSE" -X POST
curl "http://localhost:8080/students"
```
📸 **Screenshot**: terminal output of `mvn clean package` showing `BUILD SUCCESS` and the test results (`Tests run: 3, Failures: 0`).

---

## 3. Docker — containerize and run

Build the image:
```bash
docker build -t student-mgmt:latest .
```

Run the container:
```bash
docker run -d --name student-mgmt-container -p 8080:8080 student-mgmt:latest
```

Verify it's accessible from the host:
```bash
docker ps
curl "http://localhost:8080/students/health"
curl -X POST "http://localhost:8080/students?name=Alice&department=IT"
curl "http://localhost:8080/students"
```
📸 **Screenshots**:
- `docker images` showing the built image
- `docker ps` showing the running container
- browser or curl output hitting `http://localhost:8080/students`

---

## 4. Jenkins — CI/CD pipeline

### Prerequisites on the Jenkins host/agent
- Jenkins itself (with the **Pipeline** plugin, included by default)
- Maven and JDK 17 available on the agent (or configured under *Manage Jenkins → Tools*)
- Docker installed on the agent, and the `jenkins` user added to the `docker` group so it can run `docker` commands:
  ```bash
  sudo usermod -aG docker jenkins
  sudo systemctl restart jenkins
  ```

### Create the pipeline job
1. Jenkins dashboard → **New Item** → name it `student-mgmt-pipeline` → select **Pipeline** → OK.
2. Under **Pipeline** section, set **Definition** to "Pipeline script from SCM".
3. **SCM**: Git → paste your repo URL (`https://github.com/<your-username>/student-mgmt.git`).
4. **Branch**: `*/main`.
5. **Script Path**: `Jenkinsfile` (default, already correct).
6. Save, then click **Build Now**.

The `Jenkinsfile` in this repo runs 5 stages automatically:
1. **Checkout** — pulls the code from Git
2. **Build & Test (Maven)** — `mvn clean package`, publishes JUnit results
3. **Build Docker Image** — `docker build`
4. **Deploy Container** — removes any old container, runs a fresh one on port 8080
5. **Verify Deployment** — curls `/students/health` to confirm the app is up

📸 **Screenshot**: the Jenkins pipeline "Stage View" showing all 5 stages green, plus the console output of a successful build.

---

## Notes / troubleshooting
- If your Jenkins agent's Docker daemon needs `sudo`, either add `jenkins` to the `docker` group (above) or add `sudo` in front of the `docker` commands in the `Jenkinsfile`.
- If port 8080 is already taken on the Jenkins host, change `APP_PORT` in the `Jenkinsfile` environment block (and the `-p` mapping) to something free, e.g. `8081:8080`.
- pom.xml currently targets Java 17 — make sure `JAVA_HOME` on the Jenkins agent points to a JDK 17+ install.
