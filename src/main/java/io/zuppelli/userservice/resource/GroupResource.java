package io.zuppelli.userservice.resource;

import com.datastax.driver.core.PagingState;
import io.zuppelli.userservice.exception.BadRequestException;
import io.zuppelli.userservice.exception.EntityNotFoundException;
import io.zuppelli.userservice.model.Group;
import io.zuppelli.userservice.model.Page;
import io.zuppelli.userservice.model.Role;
import io.zuppelli.userservice.repository.GroupRepository;
import io.zuppelli.userservice.repository.RoleRepository;
import io.zuppelli.userservice.repository.UsersByGroupRepository;
import io.zuppelli.userservice.repository.UsersByRoleRepository;
import io.zuppelli.userservice.resource.dto.GroupDTO;
import io.zuppelli.userservice.resource.dto.PageDTO;
import io.zuppelli.userservice.service.GroupService;
import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.util.encoders.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.cassandra.core.query.CassandraPageRequest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/groups")
public class GroupResource {
    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private GroupService groupService;

    @Autowired
    private UsersByGroupRepository usersByGroupRepository;

    @Autowired
    private UsersByRoleRepository usersByRoleRepository;

    @PostMapping
    public Group add(@RequestBody GroupDTO dto) {
        if(StringUtils.isBlank(dto.getName())) throw new BadRequestException();
        return groupService.builder().add("name", dto.getName()).build();
    }

    @GetMapping("/{group}")
    public Group get(@PathVariable Group group){
        return group;
    }

    @PutMapping("/{group}")
    public Group update(@PathVariable Group group, @RequestBody GroupDTO dto){
        group.setName(dto.getName());

        return groupRepository.save(group);
    }

    @DeleteMapping("/{group}")
    public boolean delete(@PathVariable Group group) {
        if(null == group) {
            throw new EntityNotFoundException();
        }

        groupService.delete(group);

        return true;
    }

    @GetMapping
    public Page<Group> list(String hash, boolean next, boolean prev) {
        Page<Group> page = new Page<>();
        Pageable pageRequest = CassandraPageRequest.of(0,5);

        if(null != hash) {
            PagingState pagingState = PagingState.fromBytes(Base64.decode(hash));
            pageRequest = CassandraPageRequest.of(pageRequest, pagingState);

            if(next) pageRequest = pageRequest.next();
        }

        Slice<Group> slice = groupRepository.findAll(pageRequest);

        page.setElements(slice.getContent());
        page.setNext(slice.hasNext());
        page.setPageHash((CassandraPageRequest) slice.getPageable(), hash);

        return page;
    }


}
