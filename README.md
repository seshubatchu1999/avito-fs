# Avito Document Processing Platform (Java Spring Boot + Angular)

Full-stack enterprise application for logistics document extraction, smart packing list grouping, House/Master Bill of Lading (HBL/MBL) PDF generation, and vendor invoice spreadsheet export with PostgreSQL persistence and JWT authentication.

---

## Directory Structure

This project is organized into two primary folders:

```text
C:\projects\avito\document_processing_poc_java\
├── backend/                  # Java Spring Boot 3 Backend
│   ├── src/                  # Controllers, Services, Entities, Repositories, Security
│   ├── pom.xml               # Maven configuration & dependencies
│   ├── docker-compose.yml    # PostgreSQL container setup
│   ├── HBL_MULTI_TEMPLATE.pdf# Template PDF for HBL generation
│   └── mbl_final_updated.pdf # Template PDF for MBL generation
├── frontend/                 # Modern Angular 19+ Frontend
│   ├── src/                  # App components, services, interceptors, models
│   ├── package.json          # Node dependencies & build scripts
│   └── angular.json          # Angular CLI workspace configuration
└── README.md                 # System overview and quick-start instructions
```

---

## Backend (Java Spring Boot)

### Technologies
- **Java 21 / 23**
- **Spring Boot 3.3.4** (Spring Web, Spring Security, Spring Data JPA, Spring Validation)
- **PostgreSQL** database (H2 in-memory dev profile also available)
- **Google Cloud Vertex AI Gemini** SDK (`com.google.cloud:google-cloud-vertexai`)
- **Apache PDFBox 3.0** (High-precision coordinate overlay on official templates)
- **Apache POI 5.3** (Formatted Excel `.xlsx` and `.csv` generation)
- **JJWT 0.12** (Stateless Bearer JWT tokens with BCrypt password hashing)

### Running the Backend
1. **Start PostgreSQL**:
   ```powershell
   cd backend
   docker compose up -d
   ```
2. **Set Google Cloud Credentials** (Optional for Vertex AI multimodal extraction):
   ```powershell
   $env:GOOGLE_APPLICATION_CREDENTIALS = "C:\path\to\your-gcp-key.json"
   $env:GOOGLE_CLOUD_PROJECT = "your-gcp-project-id"
   $env:GOOGLE_CLOUD_LOCATION = "us-central1"
   ```
3. **Start the Spring Boot Server**:
   ```powershell
   mvn spring-boot:run
   ```
   *The backend runs on `http://localhost:8080`.*

---

## Frontend (Angular)

### Technologies & Integrations
- **Angular 19+** standalone component architecture with Angular Signals.
- **Port Integration**: Configured in `src/environments/environment.ts` pointing to `http://localhost:8080`.
- **JWT Auth Interceptor**: `auth.interceptor.ts` automatically attaches `Authorization: Bearer <token>` to outbound API requests.
- **Role & Permission Guarding**: Reactive user profile state enforcing `can_invoice_processing` and `can_mbl_processing` permissions.
- **Interactive UI**:
  - Sign In / Register dialog.
  - Admin User & Permission management modal to toggle MBL and Invoice access in real-time.
  - Interactive HBL grouping board, document previewer, and draft generation.

### Running the Frontend
```powershell
cd frontend
npm start
```
*The frontend opens at `http://localhost:4200`.*

---

## API Endpoints Reference

| Method | Path | Description |
| :--- | :--- | :--- |
| `GET` | `/health` | Service health status. |
| `POST` | `/v1/auth/register` | Register new user. First user automatically becomes Admin. |
| `POST` | `/v1/auth/login` | Login and receive Bearer JWT access token and user profile. |
| `GET` | `/v1/auth/me` | Current authenticated user profile & permissions. |
| `PATCH` | `/v1/users/{username}/permissions` | Update module permissions (Admin only). |
| `GET` | `/v1/users` | List all registered users (Admin only). |
| `POST` | `/v1/extractions/batch` | Upload and extract packing lists / invoices (Multipart). |
| `POST` | `/v1/hbls` | Generate House Bill of Lading (HBL) PDF. |
| `POST` | `/v1/hbl/preview` | Preview draft HBL PDF (Base64). |
| `POST` | `/v1/hbl-groups/reassign` | Reassign documents between HBL groups. |
| `POST` | `/v1/mbls` | Generate Master Bill of Lading (MBL) PDF. |
| `POST` | `/v1/mbl/preview` | Preview draft MBL PDF (Base64). |
| `POST` | `/v1/invoice-sheets` | Export extracted batch to XLSX or CSV spreadsheet. |

---

## Verifying the Builds

- **Backend**:
  ```powershell
  cd backend
  mvn test
  ```
- **Frontend**:
  ```powershell
  cd frontend
  npm run build
  ```
