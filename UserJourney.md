# User Journey Planner

## Sign Up

[USER SERVICE]

POST: **/api/v1/users**

---
## Get User Information

[USER SERVICE]

GET: **/api/v1/users** - Include User ID in header

---
## Update User Information

[USER SERVICE]

PUT: **/api/v1/users** - Include User ID in header

---
## Log In

[USER SERVICE]

POST: **/api/v1/auth/login**

---
## Log Out

[USER SERVICE]

POST: **/api/v1/auth/logout**

---
## Add Bank

[TRUELAYER SERVICE]

- GET: **/api/v1/bank/add** - Include User ID in state request parameter

- GET: **/api/v1/bank/callback** - Include User ID in state request parameter

- Saves the users tokens to Token table and adds access token to Redis cache for future TrueLayer service calls

---
## Update Tracked Accounts From Bank

[TRUELAYER SERVICE]

- GET: **/api/v1/bank/update** - Include User ID in header
- Request all Accounts From Truelayer Service
- Request all Cards from Truelayer Service

For each account returned:

- Kafka: Send Account/Card Information
- Account Service: Add or Update Account
- Truelayer Service: Request Account/Card Balance
- Kafka: Send Account Balance Information
- Account Service: Update Account Balance

---
## Get Accounts

[ACCOUNT SERVICE]

- GET: **/api/v1/accounts** - Include User ID in header

---
## Get Account By ID

[ACCOUNT SERVICE]

- GET: **/api/v1/accounts/{accountId}** - Include User ID in header

---
## Update Account By ID (Enriched manual data from User)

[ACCOUNT SERVICE]

- PUT: **/api/v1/accounts/{accountId}** - Include User ID in header