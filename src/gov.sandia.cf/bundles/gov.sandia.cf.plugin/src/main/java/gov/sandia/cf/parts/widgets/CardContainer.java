/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.widgets;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

/**
 * The card container to display card widgets
 * 
 * @author Didier Verstraete
 *
 */
public class CardContainer extends Composite {

	// landscape to portrait breaking coeff
	private static final float BREAK_COEFF = 0.6F;

	private Map<String, CardWidget> cards;

	/**
	 * @param parent the parent composite
	 * @param style  the style
	 */
	public CardContainer(Composite parent, int style) {
		super(parent, style);

		cards = new HashMap<>();

		this.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		GridLayout gdImage = new GridLayout();
		gdImage.makeColumnsEqualWidth = true;
		gdImage.numColumns = 1;
		gdImage.horizontalSpacing = 10;
		gdImage.verticalSpacing = 10;
		this.setLayout(gdImage);
		this.addControlListener(new ControlAdapter() {
			@Override
			public void controlResized(final ControlEvent e) {
				repaint();
			}
		});
	}

	/**
	 * @param text       the text to display
	 * @param iconName   the icon name
	 * @param background the background color
	 * @param active     is active? or not?
	 * @return the newly created tile
	 */
	public CardWidget addCard(String text, String iconName, final Color background, boolean active) {
		CardWidget tile = new CardWidget(this, SWT.NONE);
		cards.put(text, tile);
		repaint();
		return tile;
	}

	/**
	 * @return the list of cards
	 */
	public List<CardWidget> getCards() {
		return (List<CardWidget>) cards.values();
	}

	/**
	 * @param text the index of the card
	 * @return the card for index text
	 */
	public CardWidget getCard(String text) {
		return cards.getOrDefault(text, null);
	}

	/**
	 * Reload the card container and layout
	 */
	private void repaint() {

		int width = getSize().x;
		int limitWidth = (int) (width * BREAK_COEFF);
		int height = getSize().y;

		if (limitWidth > height) {
			int cardHeight = getSize().y - (((GridLayout) getLayout()).marginHeight * 2);
			if (((GridLayout) getLayout()).numColumns != cards.size() && cardHeight > 0) {
				((GridLayout) getLayout()).numColumns = cards.size();
				for (CardWidget card : cards.values()) {
					((GridData) card.getLayoutData()).heightHint = cardHeight;
					card.layout();
				}
			}
		} else {
			int nbCards = cards.size() > 1 ? cards.size() / 2 : 1;
			int cardHeight = (getSize().y / 2) - (((GridLayout) getLayout()).marginHeight * 2);
			if (((GridLayout) getLayout()).numColumns != nbCards && cardHeight > 0) {
				((GridLayout) getLayout()).numColumns = nbCards;
				cards.values().forEach(card -> {
					((GridData) card.getLayoutData()).minimumHeight = cardHeight;
					card.layout();
				});
			}
		}
	}

}
