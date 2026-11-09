package springbootblog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import springbootblog.model.ContactMessage;

public interface ContactMessageRepository extends JpaRepository<ContactMessage, Long> {
}