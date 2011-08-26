package org.springframework.samples.travel.services;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.samples.travel.domain.Booking;
import org.springframework.samples.travel.domain.Hotel;
import org.springframework.samples.travel.domain.User;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.List;

/**
 * A JPA 2-based implementation of the {@link BookingService} .
 * Delegates to a JPA {@link EntityManager} to issue data access calls against the backing database.
 * <p/>
 * The EntityManager reference is provided by the managing container (Spring) automatically.
 * <p/>
 * This class specifically avoids delegating to a separate {@link Repository}, as such a composition would
 * be redundant in this case. There is no need for the extra level of indirection, especially given how high-level JPA already is.
 * Indeed, such a composition would be largely a formality.
 */
@Service("bookingService")
public class JpaBookingService implements BookingService {

	/**
	 * this is provided by the {@link org.springframework.orm.jpa.LocalEntityManagerFactoryBean} which is configured by the managing container (Spring).
	 */
	@PersistenceContext private EntityManager entityManager;

	/**
	 * Region names
	 */
	static final private String HOTELS_REGION = "hotelsRegion";
	static final private String BOOKING_REGION = "bookingsRegion";
	static final private String USER_REGION = "usersRegion";


	private Log log = LogFactory.getLog(getClass());

	@Transactional(readOnly = true)
	@SuppressWarnings("unused")
	public User findUserById(Long id) {
		return entityManager.find(User.class, id);
	}

	@Transactional(readOnly = true)
	public Booking findBookingById(Long id) {
		return entityManager.find(Booking.class, id);
	}

	@Transactional(readOnly = true)
	public List<Booking> findBookings(String username) {
		String query = String.format("select b from %s b where b.user.username = :username order by b.checkinDate", Booking.class.getName());
		return entityManager.createQuery(query, Booking.class).setParameter("username", username).getResultList();
	}

	@Transactional(readOnly = true)
	public List<Hotel> findHotels(SearchCriteria criteria) {

		String pattern = getSearchPattern(criteria);

		if (log.isDebugEnabled()) {
			log.debug("searching hotels with search pattern: " + pattern);
		}

		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();

		CriteriaQuery<Hotel> hotelCriteriaQuery = criteriaBuilder.createQuery(Hotel.class);

		Root<Hotel> from = hotelCriteriaQuery.from(Hotel.class);

		Expression<String> city = from.get("city");
		Expression<String> zip = from.get("zip");
		Expression<String> address = from.get("address");
		Expression<String> name = from.get("name");
		Expression<Double> price = from.get("price");

		Predicate predicate = criteriaBuilder.or(
				                                        criteriaBuilder.like(criteriaBuilder.lower(city), pattern),
				                                        criteriaBuilder.like(criteriaBuilder.lower(zip), pattern),
				                                        criteriaBuilder.like(criteriaBuilder.lower(address), pattern),
				                                        criteriaBuilder.like(criteriaBuilder.lower(name), pattern));

		if (criteria.getMaximumPrice() > 0) {
			predicate = criteriaBuilder.and(predicate, criteriaBuilder.lessThanOrEqualTo(price, criteria.getMaximumPrice()));
		}

		hotelCriteriaQuery.where(predicate);

		TypedQuery<Hotel> typedQuery = entityManager.createQuery(hotelCriteriaQuery);

		if (criteria.getPage() > 0 && criteria.getPageSize() > 0) {
			typedQuery.setMaxResults(criteria.getPageSize()).setFirstResult(criteria.getPage() * criteria.getPageSize());
		}

		List<Hotel> hotels = typedQuery.getResultList();

		log.debug("returned " + hotels.size() + " results");
		return hotels;
	}


	@Cacheable(value = HOTELS_REGION)
	@Transactional(readOnly = true)
	public Hotel findHotelById(Long id) {
		return entityManager.find(Hotel.class, id);
	}

	@Cacheable(value = BOOKING_REGION, key = "#p0")
	@Transactional
	public Booking createBooking(Long hotelId, String username) {
		Hotel hotel = entityManager.find(Hotel.class, hotelId);
		User user = findUser(username);
		Booking booking = new Booking(hotel, user);
		entityManager.persist(booking);
		return booking;
	}


	@Override
	@Transactional
	public void persistBooking(Booking booking) {
		entityManager.merge(booking);
	}

	@CacheEvict(value = BOOKING_REGION, allEntries = true)
	@Transactional
	public void cancelBooking(Long id) {
		Booking booking = entityManager.find(Booking.class, id);
		if (booking != null) {
			entityManager.refresh(booking);
			entityManager.remove(booking);
		}
	}

	// helpers
	private String getSearchPattern(SearchCriteria criteria) {
		if (StringUtils.hasText(criteria.getSearchString())) {
			return "%" + criteria.getSearchString().toLowerCase().replace('*', '%') + "%";
		} else {
			return "'%'";
		}
	}

	@Cacheable(value = USER_REGION, key = "#p0")
	public User findUser(String username) {
		String query = String.format("select u from %s u where u.username = :username", User.class.getName());
		return entityManager.createQuery(query, User.class)
				       .setParameter("username", username)
				       .getSingleResult();
	}

	@Override
	@Cacheable(value = USER_REGION, key = "#p0")
	public User login(String u, String pw) {
		return findUser(u);
	}
}
