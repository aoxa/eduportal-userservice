package io.zuppelli.userservice.resource;

import com.datastax.driver.core.PagingState;
import io.zuppelli.userservice.exception.EntityNotFoundException;
import io.zuppelli.userservice.exception.NotAcceptableException;
import io.zuppelli.userservice.model.Page;
import io.zuppelli.userservice.model.Role;
import io.zuppelli.userservice.repository.GroupsByRoleRepository;
import io.zuppelli.userservice.repository.RoleRepository;
import io.zuppelli.userservice.repository.UsersByRoleRepository;
import io.zuppelli.userservice.resource.dto.RoleDTO;
import org.bouncycastle.util.encoders.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.cassandra.core.query.CassandraPageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/roles")
public class RoleResource
{
    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private GroupsByRoleRepository groupsByRoleRepository;

    @Autowired
    private UsersByRoleRepository usersByRoleRepository;

    @PostMapping
    public Role addRole(@RequestBody RoleDTO dto) {
        Role role = new Role();
        role.setName(dto.getName().replace(" ", "_"));

        return roleRepository.save(role);
    }

    @GetMapping("/{role}")
    public Role getRole(@PathVariable Role role) {
        return role;
    }


    @PutMapping("/{role}")
    public Role modifyRole(@PathVariable Role role, @RequestBody RoleDTO dto) {
        role.setName(dto.getName());

        return roleRepository.save(role);
    }

    @DeleteMapping("/{role}")
    public boolean deleteRole(@PathVariable Role role) {
        if(null == role) {
            throw new EntityNotFoundException();
        }

        if(groupsByRoleRepository.findById(role.getId()).isPresent()) {
            throw new NotAcceptableException();
        }

        usersByRoleRepository.findById(role.getId())
                .ifPresent(usersByRoleRepository::delete);

        roleRepository.delete(role);

        return true;
    }


    @GetMapping
    public Page<Role> list(String hash, boolean next, boolean prev) {
        Page<Role> page = new Page<>();
        Pageable pageRequest = CassandraPageRequest.of(0,5);

        if(null != hash) {
            PagingState pagingState = PagingState.fromBytes(Base64.decode(hash));
            pageRequest = CassandraPageRequest.of(pageRequest, pagingState);

            if(next) pageRequest = pageRequest.next();
        }

        Slice<Role> slice = roleRepository.findAll(pageRequest);

        page.setElements(slice.getContent());
        page.setNext(slice.hasNext());
        page.setPageHash((CassandraPageRequest) slice.getPageable(), hash);

        return page;
    }
}
