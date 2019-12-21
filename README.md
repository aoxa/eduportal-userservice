Add new role

`curl -X POST -d '{"name":"user"}' -H "Content-Type: application/json" http://localhost:8084/roles`

Add new group

`curl -X POST -d '{"name":"user"}' -H "Content-Type: application/json" http://localhost:8084/groups`
 
Add new user 
`curl -X POST -d '{"firstName":"name", "lastName":"last", "email":"test@test.com"}' -H "Content-Type: application/json" http://localhost:8084/users`