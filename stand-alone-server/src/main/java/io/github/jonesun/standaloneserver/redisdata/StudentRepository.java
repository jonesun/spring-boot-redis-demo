package io.github.jonesun.standaloneserver.redisdata;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * @author jone.sun
 * @date 2021/1/21 17:25
 */
@Repository
public interface StudentRepository extends CrudRepository<Student, String> {

}