package blog.repository;

import blog.model.Tag;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TagRepository extends CrudRepository<Tag, Integer> {
    /**
     * поиск тега по значению
     */
    Tag findByName(String name);
}
