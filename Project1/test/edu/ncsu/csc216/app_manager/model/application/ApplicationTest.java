/**
 * 
 */
package edu.ncsu.csc216.app_manager.model.application;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import edu.ncsu.csc216.app_manager.model.application.Application.AppType;
import edu.ncsu.csc216.app_manager.model.command.Command;
import edu.ncsu.csc216.app_manager.model.command.Command.Resolution;

/**
 * Tests the Application class
 * 
 * @author Anoushka Piduru
 */
class ApplicationTest {
	/**
	 * An instance of Application.
	 */
	private Application application;

	/**
	 * Tests the construction of an Application object.
	 */
	@Test
	void testApplicationIntAppTypeStringString() {
		// Valid application
		application = new Application(1, AppType.NEW, "Summary", "Note 1");
		assertEquals(1, application.getAppId());
		assertEquals("New", application.getAppType());
		assertEquals("Summary", application.getSummary());
		assertEquals("[Review] Note 1", application.getNotes().get(0));
		// Invalid application
		Exception e1 = assertThrows(IllegalArgumentException.class, () -> new Application(-1, null, null, null));
		assertEquals(e1.getMessage(), "Application cannot be created.");
	}

	/**
	 * Tests the second Application constructor.
	 */
	@Test
	void testApplicationIntStringStringStringStringBooleanStringArrayListOfString() {
		ArrayList<String> notes = new ArrayList<>();

		notes.add("Note 1");
		notes.add("Note 2");

		Application newApp = new Application(2, Application.CLOSED_NAME, Application.A_NEW, "Summary", "Reviewer",
				false, Command.R_REVCOMPLETED, notes);

		assertEquals("Note 1", newApp.getNotes().get(0));
		assertEquals("Note 2", newApp.getNotes().get(1));

		assertEquals(2, newApp.getAppId());
		assertEquals(Application.CLOSED_NAME, newApp.getState());
		assertEquals(Application.A_NEW, newApp.getAppType());
		assertEquals("Summary", newApp.getSummary());
		assertEquals("Reviewer", newApp.getReviewer());
		assertFalse(newApp.isProcessed());
		assertEquals(Command.R_REVCOMPLETED, newApp.getResolution());
		assertEquals(2, newApp.getNotes().size());
	}

	/**
	 * Tests updateState with the ACCEPT command for Review state.
	 */
	@Test
	void testReviewAcceptUpdate() {
		ArrayList<String> notes = new ArrayList<>();
		notes.add("Note 1");
		application = new Application(1, Application.REVIEW_NAME, Application.A_NEW, "Summary", "Reviewer", false, null,
				notes);

		assertEquals("Review", application.getStateName());

		Command acceptCommand = new Command(Command.CommandValue.ACCEPT, "reviewer", null, "note");
		application.update(acceptCommand);
		assertEquals("Interview", application.getState());
		assertEquals("-Note 1\n-[Interview] note\n", application.getNotesString());
	}

	/**
	 * Tests updateState with the STANDBY command for Review state.
	 */
	@Test
	void testReviewStandbyUpdate() {
		ArrayList<String> notes = new ArrayList<>();
		notes.add("Note 1");
		application = new Application(1, Application.REVIEW_NAME, Application.A_NEW, "Summary", "Reviewer", false, null,
				notes);

		assertEquals("Review", application.getStateName());

		Command acceptCommand = new Command(Command.CommandValue.STANDBY, "reviewer", Resolution.REVCOMPLETED, "note");
		application.update(acceptCommand);
		assertEquals("ReviewCompleted", application.getResolution());
		assertEquals("Waitlist", application.getState());
		assertEquals("-Note 1\n-[Waitlist] note\n", application.getNotesString());
	}

	/**
	 * Tests updateState with the REJECT command for Review state.
	 */
	@Test
	void testReviewRejectUpdate() {
		ArrayList<String> notes = new ArrayList<>();
		notes.add("Note 1");
		application = new Application(1, Application.REVIEW_NAME, Application.A_NEW, "Summary", "Reviewer", false, null,
				notes);

		assertEquals("Review", application.getStateName());

		Command acceptCommand = new Command(Command.CommandValue.REJECT, "reviewer", Resolution.REVCOMPLETED, "note");
		application.update(acceptCommand);
		assertEquals("ReviewCompleted", application.getResolution());
		assertEquals("Closed", application.getState());
		assertEquals("-Note 1\n-[Closed] note\n", application.getNotesString());
	}

	/**
	 * Tests updateState with the ACCEPT command for Interview state.
	 */
	@Test
	void testInterviewAcceptUpdate() {
		ArrayList<String> notes = new ArrayList<>();
		notes.add("Note 1");
		application = new Application(1, Application.INTERVIEW_NAME, Application.A_OLD, "Summary", "Reviewer", false,
				null, notes);

		assertEquals("Interview", application.getStateName());

		Command acceptCommand = new Command(Command.CommandValue.ACCEPT, "reviewer", Resolution.REVCOMPLETED, "note");
		application.update(acceptCommand);
		assertEquals("reviewer", application.getReviewer());
		assertEquals("RefCheck", application.getState());
		assertEquals("-Note 1\n-[RefCheck] note\n", application.getNotesString());
	}

	/**
	 * Tests updateState with the STANDBY command for Interview state.
	 */
	@Test
	void testInterviewStandbyUpdate() {
		ArrayList<String> notes = new ArrayList<>();
		notes.add("Note 1");
		application = new Application(1, Application.INTERVIEW_NAME, Application.A_OLD, "Summary", "Reviewer", false,
				null, notes);

		assertEquals("Interview", application.getStateName());

		Command acceptCommand = new Command(Command.CommandValue.STANDBY, "Reviewer", Resolution.REVCOMPLETED, "note");
		application.update(acceptCommand);
		assertEquals("InterviewCompleted", application.getResolution());
		assertEquals("Reviewer", application.getReviewer());
		assertEquals("Waitlist", application.getState());
		assertEquals("-Note 1\n-[Waitlist] note\n", application.getNotesString());
	}

	/**
	 * Tests updateState with the REJECT command for Interview state.
	 */
	@Test
	void testInterviewRejectUpdate() {
		ArrayList<String> notes = new ArrayList<>();
		notes.add("Note 1");
		application = new Application(1, Application.INTERVIEW_NAME, Application.A_OLD, "Summary", "Reviewer", false,
				null, notes);

		assertEquals("Interview", application.getStateName());

		Command acceptCommand = new Command(Command.CommandValue.REJECT, "reviewer", Resolution.REVCOMPLETED, "note");
		application.update(acceptCommand);
		assertEquals("InterviewCompleted", application.getResolution());
		assertEquals("Closed", application.getState());
		assertEquals("-Note 1\n-[Closed] note\n", application.getNotesString());
	}

	/**
	 * Tests updateState with the REOPEN command for Closed state.
	 */
	@Test
	void testClosedReopenUpdate() {
		ArrayList<String> notes = new ArrayList<>();
		notes.add("Note 1");
		application = new Application(1, Application.CLOSED_NAME, Application.A_NEW, "Summary", "Reviewer", false,
				Command.R_REVCOMPLETED, notes);

		assertEquals("Closed", application.getStateName());

		Command acceptCommand = new Command(Command.CommandValue.REOPEN, "Reviewer", Resolution.REVCOMPLETED, "note");

		application.update(acceptCommand);
		assertNull("ReviewCompleted", application.getResolution());
		assertEquals("Old", application.getAppType());
		assertEquals("Review", application.getState());
		assertEquals("-Note 1\n-[Review] note\n", application.getNotesString());
	}

	/**
	 * Tests updateState with the REOPEN command for Wait list state.
	 */
	@Test
	void testWaitlistReopenUpdate() {
		ArrayList<String> notes = new ArrayList<>();
		notes.add("Note 1");
		application = new Application(1, Application.WAITLIST_NAME, Application.A_NEW, "Summary", "Reviewer", false,
				Command.R_REVCOMPLETED, notes);

		assertEquals("Waitlist", application.getStateName());

		Command acceptCommand = new Command(Command.CommandValue.REOPEN, "Reviewer", Resolution.REVCOMPLETED, "note");

		application.update(acceptCommand);
		assertNull(application.getResolution());
		assertEquals("Old", application.getAppType());
		assertEquals("Review", application.getState());
		assertEquals("-Note 1\n-[Review] note\n", application.getNotesString());
	}

	/**
	 * Tests updateState in the Wait list state with the REOPEN command when
	 * resolution is INTCOMPLETED.
	 */
	@Test
	void testWaitlistReopenUpdateIntCompleted() {
		ArrayList<String> notes = new ArrayList<>();
		notes.add("Note 1");
		application = new Application(1, Application.WAITLIST_NAME, Application.A_OLD, "Summary", "Reviewer", false,
				Command.R_INTCOMPLETED, notes);

		assertEquals("Waitlist", application.getStateName());

		Command acceptCommand = new Command(Command.CommandValue.REOPEN, "Reviewer", Resolution.REVCOMPLETED, "note");

		application.update(acceptCommand);
		assertNull(application.getResolution());
		assertEquals("Old", application.getAppType());
		assertEquals("RefCheck", application.getState());
		assertEquals("-Note 1\n-[RefCheck] note\n", application.getNotesString());
	}

	/**
	 * Tests updateState with the ACCEPT command for Reference Check state.
	 */
	@Test
	void testRefChkAcceptUpdate() {
		ArrayList<String> notes = new ArrayList<>();
		notes.add("Note 1");
		application = new Application(1, Application.REFCHK_NAME, Application.A_OLD, "Summary", "Reviewer", true,
				Command.R_INTCOMPLETED, notes);

		assertEquals("RefCheck", application.getStateName());

		Command acceptCommand = new Command(Command.CommandValue.ACCEPT, "Reviewer", Resolution.REVCOMPLETED, "note");

		application.update(acceptCommand);
		assertEquals("InterviewCompleted", application.getResolution());
		assertEquals("Old", application.getAppType());
		assertEquals("Offer", application.getState());
		assertEquals("-Note 1\n-[Offer] note\n", application.getNotesString());
	}

	/**
	 * Tests updateState with the REJECT command for Reference Check state.
	 */
	@Test
	void testRefChkRejectUpdate() {
		ArrayList<String> notes = new ArrayList<>();
		notes.add("Note 1");
		application = new Application(1, Application.REFCHK_NAME, Application.A_OLD, "Summary", "Reviewer", true,
				Command.R_INTCOMPLETED, notes);

		assertEquals("RefCheck", application.getStateName());

		Command acceptCommand = new Command(Command.CommandValue.REJECT, "Reviewer", Resolution.REVCOMPLETED, "note");

		application.update(acceptCommand);
		assertEquals("ReferenceCheckCompleted", application.getResolution());
		assertEquals("Old", application.getAppType());
		assertEquals("Closed", application.getState());
		assertEquals("-Note 1\n-[Closed] note\n", application.getNotesString());
	}

	/**
	 * Tests updateState with the ACCEPT command for Offer state.
	 */
	@Test
	void testOfferAcceptUpdate() {
		ArrayList<String> notes = new ArrayList<>();
		notes.add("Note 1");
		application = new Application(1, Application.OFFER_NAME, Application.A_OLD, "Summary", "Reviewer", true,
				Command.R_INTCOMPLETED, notes);

		assertEquals("Offer", application.getStateName());

		Command acceptCommand = new Command(Command.CommandValue.ACCEPT, "Reviewer", Resolution.REVCOMPLETED, "note");

		application.update(acceptCommand);
		assertEquals("OfferCompleted", application.getResolution());
		assertEquals("Hired", application.getAppType());
		assertEquals("Closed", application.getState());
		assertEquals("-Note 1\n-[Closed] note\n", application.getNotesString());
	}

	/**
	 * Tests updateState with the REJECT command for Offer state.
	 */
	@Test
	void testOfferRejectUpdate() {
		ArrayList<String> notes = new ArrayList<>();
		notes.add("Note 1");
		application = new Application(1, Application.OFFER_NAME, Application.A_OLD, "Summary", "Reviewer", true,
				Command.R_INTCOMPLETED, notes);

		assertEquals("Offer", application.getStateName());

		Command acceptCommand = new Command(Command.CommandValue.REJECT, "Reviewer", Resolution.REVCOMPLETED, "note");

		application.update(acceptCommand);
		assertEquals("OfferCompleted", application.getResolution());
		assertNull(application.getReviewer());
		assertEquals("Old", application.getAppType());
		assertEquals("Closed", application.getState());
		assertEquals("-Note 1\n-[Closed] note\n", application.getNotesString());
	}

	/**
	 * Tests the Closed state with invalid values.
	 */
	@Test
	void testClosedInvalid() {
		ArrayList<String> notes = new ArrayList<>();
		notes.add("Note 1");
		application = new Application(1, Application.CLOSED_NAME, Application.A_OLD, "Summary", "Reviewer", true,
				Command.R_INTCOMPLETED, notes);

		Command acceptCommand = new Command(Command.CommandValue.REOPEN, "Reviewer", Resolution.INTCOMPLETED,
				"Reviewer");

		assertThrows(UnsupportedOperationException.class, () -> application.update(acceptCommand));
	}
}
