# Splunk Queries for User Management API

This document contains Splunk queries for monitoring and analyzing the User Management API performance, errors, and usage patterns.

## Table of Contents
1. [API Response Time Monitoring](#api-response-time-monitoring)
2. [Error Rate Analysis](#error-rate-analysis)
3. [Request Volume Metrics](#request-volume-metrics)
4. [User Activity Tracking](#user-activity-tracking)
5. [Performance Monitoring](#performance-monitoring)
6. [Security and Audit Queries](#security-and-audit-queries)

## API Response Time Monitoring

### Average Response Time by Endpoint
```splunk
index=application source="*crudapp*" 
| rex field=_raw "(?<method>GET|POST|PUT|DELETE)\s+(?<endpoint>/api/users[^\s]*)" 
| rex field=_raw "completed in (?<response_time>\d+)ms" 
| stats avg(response_time) as avg_response_time by endpoint, method 
| sort -avg_response_time
```

### 95th Percentile Response Time
```splunk
index=application source="*crudapp*" 
| rex field=_raw "(?<method>GET|POST|PUT|DELETE)\s+(?<endpoint>/api/users[^\s]*)" 
| rex field=_raw "completed in (?<response_time>\d+)ms" 
| stats perc95(response_time) as p95_response_time by endpoint, method 
| sort -p95_response_time
```

### Response Time Trend Over Time
```splunk
index=application source="*crudapp*" 
| rex field=_raw "(?<method>GET|POST|PUT|DELETE)\s+(?<endpoint>/api/users[^\s]*)" 
| rex field=_raw "completed in (?<response_time>\d+)ms" 
| timechart span=5m avg(response_time) as avg_response_time by endpoint
```

## Error Rate Analysis

### HTTP Error Rate by Status Code
```splunk
index=application source="*crudapp*" 
| rex field=_raw "HTTP/1.1\"\s+(?<status_code>\d+)" 
| stats count by status_code 
| eval error_category=case(
    status_code>=200 AND status_code<300, "Success",
    status_code>=400 AND status_code<500, "Client Error",
    status_code>=500, "Server Error",
    1=1, "Other"
) 
| stats sum(count) as total_requests by error_category
```

### Exception Rate Analysis
```splunk
index=application source="*crudapp*" level=ERROR
| rex field=_raw "(?<exception_type>[A-Za-z]+Exception)"
| stats count by exception_type
| sort -count
```

### Error Rate Over Time
```splunk
index=application source="*crudapp*"
| rex field=_raw "HTTP/1.1\"\s+(?<status_code>\d+)"
| eval is_error=if(status_code>=400, 1, 0)
| timechart span=5m avg(is_error) as error_rate
```

## Request Volume Metrics

### Total Requests by Endpoint
```splunk
index=application source="*crudapp*"
| rex field=_raw "(?<method>GET|POST|PUT|DELETE)\s+(?<endpoint>/api/users[^\s]*)"
| stats count by endpoint, method
| sort -count
```

### Requests Per Minute
```splunk
index=application source="*crudapp*"
| rex field=_raw "(?<method>GET|POST|PUT|DELETE)\s+(?<endpoint>/api/users[^\s]*)"
| timechart span=1m count by method
```

### Peak Traffic Analysis
```splunk
index=application source="*crudapp*"
| rex field=_raw "(?<method>GET|POST|PUT|DELETE)\s+(?<endpoint>/api/users[^\s]*)"
| timechart span=1h count as requests
| sort -requests
| head 10
```

## User Activity Tracking

### User Creation Patterns
```splunk
index=application source="*crudapp*" "POST /api/users"
| rex field=_raw "User created successfully with ID: (?<user_id>\d+)"
| timechart span=1h count as new_users
```

### User Deletion Tracking
```splunk
index=application source="*crudapp*" "DELETE /api/users"
| rex field=_raw "User deleted successfully with ID: (?<user_id>\d+)"
| timechart span=1h count as deleted_users
```

### Most Active User Operations
```splunk
index=application source="*crudapp*"
| rex field=_raw "(?<operation>Creating|Retrieving|Updating|Deleting) user"
| stats count by operation
| sort -count
```

## Performance Monitoring

### Database Query Performance
```splunk
index=application source="*crudapp*" "Hibernate:"
| rex field=_raw "took (?<query_time>\d+)ms"
| stats avg(query_time) as avg_query_time, max(query_time) as max_query_time, count as query_count
```

### Memory Usage Monitoring
```splunk
index=application source="*crudapp*" "Memory"
| rex field=_raw "Memory usage: (?<memory_usage>\d+)MB"
| timechart span=5m avg(memory_usage) as avg_memory_usage
```

### Slow Query Detection
```splunk
index=application source="*crudapp*"
| rex field=_raw "completed in (?<response_time>\d+)ms"
| where response_time > 1000
| stats count by _time, response_time
| sort -response_time
```

## Security and Audit Queries

### Failed Authentication Attempts
```splunk
index=application source="*crudapp*" (status_code=401 OR status_code=403)
| rex field=_raw "(?<client_ip>\d+\.\d+\.\d+\.\d+)"
| stats count by client_ip
| sort -count
```

### Suspicious Activity Detection
```splunk
index=application source="*crudapp*"
| rex field=_raw "(?<client_ip>\d+\.\d+\.\d+\.\d+)"
| rex field=_raw "(?<method>GET|POST|PUT|DELETE)"
| stats count by client_ip, method
| where count > 100
| sort -count
```

### Data Access Audit
```splunk
index=application source="*crudapp*" ("GET /api/users" OR "POST /api/users" OR "PUT /api/users" OR "DELETE /api/users")
| rex field=_raw "(?<method>GET|POST|PUT|DELETE)\s+(?<endpoint>/api/users[^\s]*)"
| rex field=_raw "(?<client_ip>\d+\.\d+\.\d+\.\d+)"
| stats count by client_ip, method, endpoint
| sort -count
```

## Custom Business Metrics

### User Growth Rate
```splunk
index=application source="*crudapp*" "User created successfully"
| timechart span=1d count as daily_signups
| streamstats current=f last(daily_signups) as previous_day
| eval growth_rate=round(((daily_signups-previous_day)/previous_day)*100, 2)
| fields _time, daily_signups, growth_rate
```

### API Health Score
```splunk
index=application source="*crudapp*"
| rex field=_raw "HTTP/1.1\"\s+(?<status_code>\d+)"
| rex field=_raw "completed in (?<response_time>\d+)ms"
| eval success=if(status_code>=200 AND status_code<300, 1, 0)
| eval fast_response=if(response_time<500, 1, 0)
| stats avg(success) as success_rate, avg(fast_response) as performance_score
| eval health_score=round((success_rate + performance_score)/2 * 100, 2)
| fields health_score, success_rate, performance_score
```

### User Engagement Metrics
```splunk
index=application source="*crudapp*"
| rex field=_raw "(?<operation>GET|PUT) /api/users/(?<user_id>\d+)"
| stats dc(user_id) as active_users, count as total_operations
| eval avg_operations_per_user=round(total_operations/active_users, 2)
| fields active_users, total_operations, avg_operations_per_user
```
