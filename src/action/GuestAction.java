package action;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import model.Booking;
import model.Guest;
import model.Structure;
import model.User;
import model.UserAware;
import model.internal.Message;


import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Actions;
import org.apache.struts2.convention.annotation.InterceptorRef;
import org.apache.struts2.convention.annotation.InterceptorRefs;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.interceptor.SessionAware;
import org.springframework.beans.factory.annotation.Autowired;

import service.BookingService;
import service.GuestService;
import service.UserService;

import com.opensymphony.xwork2.ActionSupport;

@ParentPackage( value="default")
@InterceptorRefs({
	@InterceptorRef("userAwareStack")    
})
@Result(name="notLogged", location="/homeNotLogged.jsp")
public class GuestAction extends ActionSupport implements SessionAware,UserAware{
	private Map<String, Object> session = null;
	private List<Guest> guests = null;
	private Guest guest = null;
	private Integer id;
	private Message message = new Message();
	private List<Booking> bookings = null;
	private String term;
	private List<Integer> years = null;
	private Integer idStructure = null;
	
	@Autowired
	private GuestService guestService = null;
	@Autowired
	private BookingService bookingService = null;
	
	@Actions({
		@Action(value="/findAllGuests",results = {
				@Result(name="success",location="/guests.jsp")
		}) 
	})
	public String findAllGuests(){
		
		List<Integer> listYears =new ArrayList<Integer>();
		
		for (int i=2012; i>1900; i--) {
			listYears.add(i);
		}
		
		this.setYears(listYears);
		this.setGuests(
				this.getGuestService().findGuestsByIdStructure(
						this.getIdStructure()));
		return SUCCESS;		
	}
	
	@Actions({
		@Action(value="/findAllGuestsJson",results = {
				@Result(type ="json",name="success", params={
						"root","guests"
				})}) ,
				@Action(value="/findAllGuestsFiltered",results = {
						@Result(name="success",location="/guests.jsp")
				})
	})
	public String findAllGuestsFiltered() {
		
		List<Guest> allGuests = null;
		List<Guest> returnedGuests = new ArrayList<Guest>();

		
		List<Integer> listYears =new ArrayList<Integer>();
		
		for (int i=2012; i>1900; i--) {
			listYears.add(i);
		}
		
		this.setYears(listYears);
		if (this.getTerm() != null && this.getTerm().length() > 1) {
			allGuests = this.getGuestService().findGuestsByIdStructure(
					this.getIdStructure());

			for (Guest guest : allGuests) {
				if (guest.getLastName().toLowerCase()
						.contains(this.getTerm().toLowerCase())) {
					returnedGuests.add(guest);
				}
			}
		}
		this.setGuests(returnedGuests);
		return SUCCESS;
	}
	
	@Actions({
		@Action(value="/findAllGuestsByName",results = {
				@Result(name="success",location="/guests.jsp")
		}) 
	})
	public String findAllGuestsByName() {
		
		List<Guest> allGuests = null;
		List<Guest> returnedGuests = null;

	
		
		List<Integer> listYears =new ArrayList<Integer>();
		
		for (int i=2012; i>1900; i--) {
			listYears.add(i);
		}
		
		this.setYears(listYears);
		returnedGuests = new ArrayList<Guest>();
		if (this.getTerm() != null && this.getTerm().length() > 1) {
			allGuests = this.getGuestService().findGuestsByIdStructure(this.getIdStructure());
			for (Guest guest : allGuests) {
				String allName = guest.getFirstName().toLowerCase()
						+ guest.getLastName().toLowerCase();
				if (allName.equals(this.getTerm().toLowerCase())) {
					returnedGuests.add(guest);
				}
			}
		}
		this.setGuests(returnedGuests);
		return SUCCESS;
	}
	
	@Actions({
		@Action(value="/findGuestById",results = {
				@Result(type ="json",name="success", params={
						"excludeProperties","session,guests,id,guestService,bookingService"
				}) ,
				@Result(type ="json",name="error", params={
						"root","message"
				})}) 
	})	
	public String findGuestById(){
//		User user = null;
		Guest aGuest = null;
		
//		user = (User)session.get("user");
		List<Integer> listYears =new ArrayList<Integer>();
		
		for (int i=2012; i>1900; i--) {
			listYears.add(i);
		}
		
		this.setYears(listYears);
		aGuest = this.getGuestService().findGuestById(this.getId());
		if(aGuest != null){
			this.setGuest(aGuest);
			this.getMessage().setResult(Message.SUCCESS);
			return SUCCESS;
		}
		this.getMessage().setResult(Message.ERROR);
		this.getMessage().setDescription(getText("guestNotFoundError"));
		return ERROR;
	}
	
	@Actions({
		@Action(value="/goUpdateGuest",results = {
				@Result(name="success",location="/guest_edit.jsp")
		})
	})
	public String goUpdateGuest() {
		
		List<Integer> listYears =new ArrayList<Integer>();
		
		for (int i=2012; i>1900; i--) {
			listYears.add(i);
		}
		
		this.setYears(listYears);
		this.setGuest(this.getGuestService().findGuestById(this.getId())); 
		this.setBookings(this.getBookingService().findBookingsByIdBooker(this.getId()));
		
		return SUCCESS;
	}
	
	@Actions({
		@Action(value="/saveUpdateGuest",results = {
				@Result(type ="json",name="success", params={
						"root","message"
				})
		})	
	})
	public String saveUpdateGuest(){
		
		Guest oldGuest = null;
		
		oldGuest = this.getGuestService().findGuestById(this.getGuest().getId());
		this.getGuest().setId_structure(this.getIdStructure());
		if(oldGuest == null){
			//Si tratta di una aggiunta
			this.getGuestService().insertGuest(this.getGuest());
			this.getMessage().setResult(Message.SUCCESS);
			this.getMessage().setDescription(getText("guestAddSuccessAction"));
			
		}else{
			//Si tratta di un update
			this.getGuestService().updateGuest(this.getGuest());
			this.getMessage().setResult(Message.SUCCESS);
			this.getMessage().setDescription(getText("guestUpdateSuccessAction"));
		}
		return SUCCESS;		
	}
	
	@Actions({
		@Action(value="/deleteGuest",results = {
				@Result(type ="json",name="success", params={
						"root","message"
				}),
				@Result(type ="json",name="error", params={
						"root","message"
				})
		})
	})
	public String deleteGuest(){

		Integer id_guest = 0;
		Integer count = 0;
		
		id_guest = this.getId();
		
		if(this.getBookingService().countBookingsByIdGuest(id_guest) > 0){
			this.getMessage().setResult(Message.ERROR);
			this.getMessage().setDescription("Non è possibile cancellare il Guest perchè esistono dei Booking Associati a questo Guest");
			return ERROR;
		}
		count = this.getGuestService().deleteGuest(id_guest);
		if(count>0){
			this.getMessage().setResult(Message.SUCCESS);
			this.getMessage().setDescription(getText("guestDeleteSuccessAction"));
			return SUCCESS;
		}else{
			this.getMessage().setResult(Message.ERROR);
			this.getMessage().setDescription(getText("guestDeleteErrorAction"));
			return ERROR;
		}
		
	}

	public Message getMessage() {
		return message;
	}
	public void setMessage(Message message) {
		this.message = message;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Map<String, Object> getSession() {
		return session;
	}
	@Override
	public void setSession(Map<String, Object> session) {
		this.session = session;
	}
	public List<Guest> getGuests() {
		return guests;
	}
	public void setGuests(List<Guest> guests) {
		this.guests = guests;
	}
	public Guest getGuest() {
		return guest;
	}
	public void setGuest(Guest guest) {
		this.guest = guest;
	}
	public List<Booking> getBookings() {
		return bookings;
	}
	public void setBookings(List<Booking> bookings) {
		this.bookings = bookings;
	}
	public String getTerm() {
		return term;
	}
	public void setTerm(String term) {
		this.term = term;
	}
	public List<Integer> getYears() {
		return years;
	}
	public void setYears(List<Integer> years) {
		this.years = years;
	}

	public GuestService getGuestService() {
		return guestService;
	}
	public void setGuestService(GuestService guestService) {
		this.guestService = guestService;
	}
	public BookingService getBookingService() {
		return bookingService;
	}
	public void setBookingService(BookingService bookingService) {
		this.bookingService = bookingService;
	}

	public Integer getIdStructure() {
		return idStructure;
	}

	public void setIdStructure(Integer idStructure) {
		this.idStructure = idStructure;
	}
	
	
}