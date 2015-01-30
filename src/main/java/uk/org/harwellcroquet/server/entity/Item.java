package uk.org.harwellcroquet.server.entity;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import uk.org.harwellcroquet.shared.BadInputException;
import uk.org.harwellcroquet.shared.ItemTO;
import uk.org.harwellcroquet.shared.UserTO;

@Entity
@NamedQueries({
		@NamedQuery(name = "ItemByType", query = "SELECT i FROM Item i WHERE i.type = :type ORDER BY i.seq"),
		@NamedQuery(name = "ItemByTypeDateOrdered", query = "SELECT i FROM Item i WHERE i.type = :type ORDER BY i.date desc, i.seq"),
		@NamedQuery(name = "ItemByTypeAndUser", query = "SELECT i FROM Item i WHERE i.type = :type AND i.user.id = :user ORDER BY i.seq"),
		@NamedQuery(name = "MaxSeq", query = "SELECT MAX(i.seq) FROM Item i WHERE i.type = :type AND i.user.id = :user"),
		@NamedQuery(name = "AllItems", query = "SELECT i FROM Item i") })
public class Item {

	private static Set<String> goodTokens = new HashSet<String>(Arrays.asList("p", "br", "ol", "ul", "li", "em", "h2",
			"h3", "a"));

	@Column(length = 4096)
	private String content;

	@Temporal(TemporalType.DATE)
	private Date date;

	@Id
	@GeneratedValue
	private Long id;

	private int seq;

	@Column(length = 16)
	private String type;

	@ManyToOne
	private User user;

	// For JPA
	public Item() {
	}

	public Item(ItemTO itemTO) throws BadInputException {
		this.id = itemTO.getId();
		UserTO userTO = itemTO.getUserTO();
		if (userTO != null) {
			this.user = new User(itemTO.getUserTO());
		}
		this.date = itemTO.getDate();
		this.type = itemTO.getType();
		this.seq = itemTO.getSeq();
		this.setContent(itemTO.getContent());
	}

	public String getContent() {
		return this.content;
	}

	public Date getDate() {
		return this.date;
	}

	public Long getId() {
		return this.id;
	}

	public int getSeq() {
		return this.seq;
	}

	public String getType() {
		return this.type;
	}

	public User getUser() {
		return this.user;
	}

	public void setContent(String content) throws BadInputException {

		SAXParserFactory spf = SAXParserFactory.newInstance();

		try {
			SAXParser sp = spf.newSAXParser();
			sp.parse(new ByteArrayInputStream(("<P>" + content + "</P>").getBytes()), new DefaultHandler() {
				@Override
				public void startElement(String uri, String localName, String qName, Attributes attributes)
						throws SAXException {
					if (!Item.goodTokens.contains(qName.toLowerCase())) {
						throw new SAXException("Html tag: " + qName + " is not allowed");
					}
				}
			});
		} catch (SAXException e) {
			throw new BadInputException("Encountered " + e.getMessage() + " processing " + content);
		} catch (ParserConfigurationException e) {
			throw new BadInputException("Encountered " + e.getMessage() + " processing " + content);
		} catch (IOException e) {
			throw new BadInputException("Encountered " + e.getMessage() + " processing " + content);
		}
		this.content = content;
	}

	public void setId(long id) {
		this.id = id;

	}

	public void setSeq(int seq) {
		this.seq = seq;

	}

	public ItemTO getTransferObject() {
		return new ItemTO(id, user.getTransferObject(), date, type, seq, content);
	}

	public void setUser(User user) {
		this.user = user;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public void setType(String type) {
		this.type = type;
	}

}
