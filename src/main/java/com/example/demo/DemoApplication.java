package com.example.demo;

import com.redis.lettucemod.api.StatefulRedisModulesConnection;
import com.redis.lettucemod.api.sync.RedisModulesCommands;
import com.redis.lettucemod.search.SearchOptions;
import com.redis.lettucemod.search.SearchResults;
import io.lettuce.core.RedisCommandExecutionException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("test")
@RestController
@SpringBootApplication
public class DemoApplication {

	private static final SearchOptions<String, String> SEARCH_OPTIONS = SearchOptions.<String, String>builder()
			.sortBy(SearchOptions.SortBy.asc("User-Name"))
			.limit(0, 10000).build();

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	private final GenericObjectPool<StatefulRedisModulesConnection<String, String>> pool;

	@GetMapping("/search")
	public List<String> searchUsernames(){
		//dummy redis search query, which needs to be registered in Spans on Tempo
		try (StatefulRedisModulesConnection<String, String> connection = pool.borrowObject()) {
			RedisModulesCommands<String, String> commands = connection.sync();
			log.info("About to execute RedisSearch query...");
			SearchResults<String, String> documents = commands.ftSearch("myIdx", "@User\\-Name:{John}", SEARCH_OPTIONS);
			log.info("RedisSearch query completed. Got {} results", documents.size());
			//will return all 'John' usernames, will be empty result since nothing is in Redis
			return documents.stream().map(document -> document.get("User-Name")).collect(Collectors.toList());
		} catch (RedisCommandExecutionException redisException) {
			log.error("Unexpected RedisCommandExecutionException ::", redisException);
		} catch (Exception e) {
			log.error("Unexpected Exception ::", e);
		}
		return List.of();
	}
}
