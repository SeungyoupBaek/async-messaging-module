# async-messaging-module

## Architecture
```
message -> AMM Queue -> AMM Scheduler -> AMM Message Consumer
```
---
### Thread pool flow
```
1. When the first task comes in, it creates a thread equal to the core size 
```
