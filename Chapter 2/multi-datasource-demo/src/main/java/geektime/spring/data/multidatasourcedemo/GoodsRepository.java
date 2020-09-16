package geektime.spring.data.multidatasourcedemo;

import org.springframework.data.repository.CrudRepository;

public interface GoodsRepository extends CrudRepository<Goods, Integer> {
}
