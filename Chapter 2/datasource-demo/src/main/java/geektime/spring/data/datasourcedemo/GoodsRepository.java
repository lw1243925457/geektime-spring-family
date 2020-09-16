package geektime.spring.data.datasourcedemo;

import org.springframework.data.repository.CrudRepository;

public interface GoodsRepository extends CrudRepository<Goods, Integer> {
}
