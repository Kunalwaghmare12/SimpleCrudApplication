package com.kunal.simplecrudapp.service.employee;

import com.kunal.simplecrudapp.dao.EmployeeDao;
import com.kunal.simplecrudapp.entity.Address;
import com.kunal.simplecrudapp.entity.Employee;
import com.kunal.simplecrudapp.exceptions.EmployeeAlreadyExistException;
import com.kunal.simplecrudapp.exceptions.EmployeeNotFoundException;
import com.kunal.simplecrudapp.repository.EmployeeRepo;
import com.kunal.simplecrudapp.service.mailservice.JavaMailService;
import com.kunal.simplecrudapp.service.mailservice.MailTemplate;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepo employeeRepo;
    private final ModelMapper modelMapper;
    private static final Logger logger = LoggerFactory.getLogger(EmployeeServiceImpl.class);
    private final JavaMailService mailService;
    private final MailTemplate mailTemplate;


    public EmployeeServiceImpl(EmployeeRepo employeeRepo, ModelMapper modelMapper, JavaMailService mailService, MailTemplate mailTemplate) {
        this.employeeRepo = employeeRepo;
        this.modelMapper=modelMapper;
        this.mailService = mailService;
        this.mailTemplate = mailTemplate;
    }

    @Override
    @Transactional
    public EmployeeDao saveEmployee(EmployeeDao employeeDao) throws EmployeeAlreadyExistException, MessagingException {
        logger.info("Inside Method : saveEmployee() --------------->>>> ");
        boolean isEmployeeExist = employeeRepo.existsByEmail(employeeDao.getEmail());
        if (!isEmployeeExist) {
            Employee e = modelMapper.map(employeeDao, Employee.class);
            employeeRepo.save(e);
            String mailTemp=mailService.loadTemplate(e.getFirstName(),e.getEmail(),mailTemplate.getEmployeeCreatedMailTemplate());
            mailService.sendHtmlEmail(e.getEmail(),mailTemplate.getEmployeeCreatedSubject(),mailTemp);
            return modelMapper.map(e,EmployeeDao.class);
        } else {
            throw new EmployeeAlreadyExistException("employee with given email already exist");
        }
    }

    @Override
    @Transactional
    public EmployeeDao updateEmployee(long id, EmployeeDao employeeDao) throws EmployeeNotFoundException, MessagingException {
        logger.info("Inside Method : updateEmployee() --------------->>>> " +id);
            Optional<Employee> existingEmployee=employeeRepo.findById(id);
            if(existingEmployee.isPresent()){
                Employee employee=existingEmployee.get();
                logger.info("employee found with "+id+" EmployeeData : "+employee.toString());
                Employee updatedInfo=modelMapper.map(employeeDao,Employee.class);
                // updating fields
                employee.setFirstName(updatedInfo.getFirstName());
                employee.setLastName(updatedInfo.getLastName());
                employee.setEmail(updatedInfo.getEmail());
                employee.setContact(updatedInfo.getContact());
                Address address=new Address();
                address.setStreet(employeeDao.getAddress().getStreet());
                address.setState(employeeDao.getAddress().getState());
                address.setCity(employeeDao.getAddress().getCity());
                address.setPincode(employeeDao.getAddress().getPincode());
                employee.setAddress(updatedInfo.getAddress());
                employee.setAddress(address);

                Employee savedUpdatedEmployee=employeeRepo.save(employee);
                String mailTemp=mailService.loadTemplate(savedUpdatedEmployee.getFirstName(),savedUpdatedEmployee.getEmail(),mailTemplate.getEmployeeUpdatedMailTemplate());
                mailService.sendHtmlEmail(savedUpdatedEmployee.getEmail(),mailTemplate.getEmployeeUpdatedSubject(),mailTemp);
                return modelMapper.map(savedUpdatedEmployee,EmployeeDao.class);
            }else{
                throw new EmployeeNotFoundException("Employee with given "+id+" not exist");
            }
    }

    @Override
    public EmployeeDao findEmployeeById(long id) throws EmployeeNotFoundException {
        logger.info("Inside Method : findEmployeeById() --------------->>>> "+id);
        Employee foundEmployee=employeeRepo.findById(id).
                orElseThrow(()->new EmployeeNotFoundException("Employee not found"));
        EmployeeDao employeeDao=modelMapper.map(foundEmployee,EmployeeDao.class);
        return employeeDao;
    }

    @Override
    public List<EmployeeDao> findAllEmployees() {
        logger.info("Inside Method : findAllEmployee() --------------->>>> ");
        List<Employee> employees=employeeRepo.findAll();
        return employees.stream().map(e->modelMapper.map(e,EmployeeDao.class)).toList();

    }

    @Override
    @Transactional
    public void deleteEmployeeById(long id) throws EmployeeNotFoundException, MessagingException {
            logger.info("Inside Method : deleteEmployeeById() --------------->>>> "+id);
//            boolean isExistById=employeeRepo.existsById(id);
            Employee e=employeeRepo.findById(id).orElseThrow(()->new EmployeeNotFoundException("employee with given id not found"));
            String email=e.getEmail();
        String mailTemp=mailService.loadTemplate(e.getFirstName(),e.getEmail(),mailTemplate.getEmployeeDeletedMailTemplate());

        employeeRepo.deleteById(id);
            mailService.sendHtmlEmail(email,mailTemplate.getEmployeeDeletedSubject(),mailTemp);

    }

    @Override
    @Retryable(value = Exception.class,maxAttempts = 3,backoff = @Backoff(1000))
    public String callThirdPartyApi() throws Exception {
        throw new Exception("error in calling service");
//        return "calling mfa-service";

    }

    @Recover
    public String recoveryMethodForApi(){
        return "calling e-otp service";
    }
}
