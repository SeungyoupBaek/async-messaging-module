# async-messaging-module

## Architecture
```
message -> AMM TaskQueue -> Task successfully finished
message -> AMM TaskQueue -> Task failed -> AMM DeadLetterQueue -> After 1 minutes retry Task
```
---
