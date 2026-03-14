package com.emapp.EmployeeManagement;

import com.emapp.EmployeeManagement.common.exception.EmailAlreadyExistsException;
import com.emapp.EmployeeManagement.employee.*;
import com.emapp.EmployeeManagement.team.Team;
import com.emapp.EmployeeManagement.team.TeamRepository;
import org.springframework.stereotype.Service;
import com.emapp.EmployeeManagement.common.exception.ResourceNotFoundException;
import com.emapp.EmployeeManagement.common.exception.InvalidOperationException;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EmployeeApplicationService {

    private final EmployeeRepository employeeRepository;
    private final TeamRepository teamRepository;

    public EmployeeApplicationService(EmployeeRepository employeeRepository, TeamRepository teamRepository) {
        this.employeeRepository = employeeRepository;
        this.teamRepository = teamRepository;
    }

    // ← ADDED
    public List<EmployeeResponse> getAllEmployees() {
        return employeeRepository.findAll()
                .stream()
                .map(emp -> new EmployeeResponse(
                        emp.getId().toString(),
                        emp.getFullname(),
                        emp.getEmail(),
                        emp.getRole()
                ))
                .toList();
    }

    @Transactional
    public EmployeeResponse createEmployee(EmployeeRequest request) {
        boolean emailExists = employeeRepository.findByEmail(request.getEmail()).isPresent();
        if (emailExists) {
            throw new EmailAlreadyExistsException(request.getEmail());
        }
        Employee employee = new Employee(
                request.getFullname(),
                request.getEmail(),
                request.getRole()
        );
        Employee savedEmployee = employeeRepository.save(employee);
        return new EmployeeResponse(
                savedEmployee.getId().toString(),
                savedEmployee.getFullname(),
                savedEmployee.getEmail(),
                savedEmployee.getRole()
        );
    }

    @Transactional
    public void assignManager(AssignManagerRequest request) {
        Employee employee = employeeRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));
        Employee manager = employeeRepository.findById(request.getManagerId())
                .orElseThrow(() -> new ResourceNotFoundException("Manager not found"));
        if (employee.getId().equals(manager.getId())) {
            throw new InvalidOperationException("Employee can't be their own manager");
        }
        if (manager.getRole() == EmployeeRole.EMPLOYEE) {
            throw new InvalidOperationException("Selected manager does not have manager privileges");
        }
        employee.setManager(manager);
    }

    public List<EmployeeResponse> getSubordinates(Long managerId) {
        Employee manager = employeeRepository.findById(managerId)
                .orElseThrow(() -> new ResourceNotFoundException("Manager not found"));
        return manager.getSubordinates()
                .stream()
                .map(emp -> new EmployeeResponse(
                        emp.getId().toString(),
                        emp.getFullname(),
                        emp.getEmail(),
                        emp.getRole()
                ))
                .toList();
    }

    @Transactional
    public void assignTeam(AssignTeamRequest request) {
        Employee employee = employeeRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));
        Team team = teamRepository.findById(request.getTeamId())
                .orElseThrow(() -> new ResourceNotFoundException("Team not found"));
        employee.setTeam(team);
    }

    public List<EmployeeResponse> getTeamMembers(Long teamId) {
        return employeeRepository.findByTeamId(teamId)
                .stream()
                .map(emp -> new EmployeeResponse(
                        emp.getId().toString(),
                        emp.getFullname(),
                        emp.getEmail(),
                        emp.getRole()
                ))
                .toList();
    }
}