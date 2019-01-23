package com.openrsc.server.plugins.quests.members.digsite;

import com.openrsc.server.Constants;
import com.openrsc.server.Constants.Quests;
import com.openrsc.server.model.Skills;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.QuestInterface;
import com.openrsc.server.plugins.listeners.action.InvUseOnNpcListener;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.executive.InvUseOnNpcExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;
import com.openrsc.server.util.rsc.MessageType;

import static com.openrsc.server.plugins.Functions.addItem;
import static com.openrsc.server.plugins.Functions.hasItem;
import static com.openrsc.server.plugins.Functions.incQuestReward;
import static com.openrsc.server.plugins.Functions.npcTalk;
import static com.openrsc.server.plugins.Functions.playerTalk;
import static com.openrsc.server.plugins.Functions.removeItem;
import static com.openrsc.server.plugins.Functions.showMenu;
import static com.openrsc.server.plugins.Functions.sleep;

public class DigsiteExpert implements QuestInterface, TalkToNpcListener, TalkToNpcExecutiveListener, InvUseOnNpcListener, InvUseOnNpcExecutiveListener {

	private static final int EXPERT = 728;

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		return n.getID() == EXPERT;
	}

	@Override
	public void onTalkToNpc(Player p, Npc n) {
		if (n.getID() == EXPERT) {
			switch (p.getQuestStage(Constants.Quests.DIGSITE)) {
				case -1:
					npcTalk(p, n, "Hello again",
						"I am now studying this mysterious altar and its inhabitants",
						"The markings are strange, but it refers to a god I have never",
						"heard of before named Zaros. It must be some pagan superstition.",
						"That was a great find, who knows what other secrets",
						"Lie buried beneath the surface of our land...");
					break;
				case 0:
				case 1:
				case 2:
				case 3:
				case 4:
				case 5:
				case 6:
					playerTalk(p, n, "Hello, who are you ?");
					npcTalk(p, n, "Good day to you",
						"My name is Terry balando",
						"I am an expert on digsite finds",
						"I am employed by the museum in varrock",
						"To oversee all finds in this digsite",
						"Anything you find must be reported to me");
					playerTalk(p, n, "Oh, okay if I find anything of interest I will bring it here");
					npcTalk(p, n, "Very good",
						"Can I help you at all ?");
					int menu = showMenu(p, n, false, //do not send over
						"I have something I need checking out",
						"No thanks",
						"Can you tell me anything about the digsite?");
					if (menu == 0) {
						playerTalk(p, n, "I have something I need checking out");
						npcTalk(p, n, "Okay, give it to me and I'll have a look for you");
					} else if (menu == 1) {
						playerTalk(p, n, "No thanks");
						npcTalk(p, n, "Good, let me know if you find anything unusual");
					} else if (menu == 2) {
						playerTalk(p, n, "Can you tell me anything about the digsite ?");
						npcTalk(p, n, "Yes indeed, I am currently studying the lives of the settlers",
							"During the end of the third age, this used to be a great city",
							"It's inhabitants were humans, supporters of the god Saradomin",
							"It's not recorded what happened to the community here",
							"I suspect nobody has lived here for over a millenium!");
					}
					break;
			}
		}
	}

	@Override
	public int getQuestId() {
		return Constants.Quests.DIGSITE;
	}

	@Override
	public String getQuestName() {
		return "Digsite (members)";
	}

	@Override
	public boolean isMembers() {
		return true;
	}

	@Override
	public void handleReward(Player player) {
		player.message("Congratulations, you have finished the digsite quest");
		player.message("@gre@You haved gained 2 quest points!");
		int[] questData = Quests.questData.get(Quests.DIGSITE);
		//keep order kosher
		int[] skillIDs = {Skills.MINING, Skills.HERBLAW};
		//1200 for mining, 500 for herblaw
		int[] amounts = {1200, 500};
		for (int i = 0; i < skillIDs.length; i++) {
			questData[Quests.MAPIDX_SKILL] = skillIDs[i];
			questData[Quests.MAPIDX_BASE] = amounts[i];
			questData[Quests.MAPIDX_VAR] = amounts[i];
			incQuestReward(player, questData, i == (skillIDs.length - 1));
		}
		player.getCache().remove("winch_rope_2");
		player.getCache().remove("winch_rope_1");
		player.getCache().remove("digsite_winshaft");
	}

	@Override
	public boolean blockInvUseOnNpc(Player p, Npc n, Item i) {
		return n.getID() == EXPERT;
	}

	@Override
	public void onInvUseOnNpc(Player p, Npc n, Item i) {
		if (n.getID() == EXPERT) {
			switch (i.getID()) {
				case 1118: // GOLD NUGGETS
					playerTalk(p, n, "I have these gold nuggets");
					if (hasItem(p, 1118, 3)) {
						p.message("You give the nuggets to the expert");
						p.getInventory().remove(1118, 3);
						addItem(p, 152, 1);
						npcTalk(p, n, "Good, that's 3, I can exchange them for normal gold now",
							"You can get this refined and make a profit!");
						playerTalk(p, n, "Excellent!");
					} else {
						npcTalk(p, n, "I can't do much with these nuggets yet",
							"Come back when you have 3, and I will exchange them with you");
						playerTalk(p, n, "Okay I will, thanks");
					}
					break;
				case 1113: // FULL PANNING TRAY
					p.message("You give the panning tray to the expert");
					npcTalk(p, n, "Have you searched this tray yet ?");
					playerTalk(p, n, "Not that I remember");
					npcTalk(p, n, "It may contain something, I don't want to get my hands dirty");
					p.playerServerMessage(MessageType.QUEST, "The expert hands the tray back to you");
					break;
				case 1112: // PANNING TRAY WITH NUGGETS
					p.message("You give the panning tray to the expert");
					npcTalk(p, n, "Did you realize there is something in this tray ?");
					playerTalk(p, n, "Err, not really");
					npcTalk(p, n, "Check it out thoroughly first");
					p.playerServerMessage(MessageType.QUEST, "The expert hands you back the tray");
					break;
				case 1111: // EMPTY PANNING TRAY
					p.message("You give the panning tray to the expert");
					npcTalk(p, n, "I have no need for panning trays");
					break;
				case 1150: // CRACKED ROCK SAMPLE
					playerTalk(p, n, "I found this rock...");
					npcTalk(p, n, "What a shame it's cracked, this looks like it would have been a good sample");
					break;
				case 1175: // TALISMAN ZAROS
					playerTalk(p, n, "What about this ?");
					npcTalk(p, n, "Unusual...",
						"This object doesn't appear right...",
						"Hmmmm.....");
					sleep(1500);
					npcTalk(p, n, "I wonder...Let me check my guide...",
						"Could it be ? surely not...",
						"From the markings on it it seems to be",
						"a ceremonial ornament to a god named...",
						"Zaros? I have never heard of him before",
						"This is a great discovery, we know very little",
						"of the pagan gods that people worshipped",
						"in the olden days. There is some strange writing",
						"embossed upon it - it says",
						"'Zaros will return and wreak his vengeance",
						"upon Zamorak the pretender' - I wonder what",
						"it means by that? Some silly superstition probably.",
						"Still, I wonder what this is doing around here...",
						"I'll tell you what, as you have found this",
						"I will allow you to use the private dig shaft",
						"You obviously have a keen eye...",
						"Take this letter and give it to one of the workmen",
						"And they will allow you to use it");
					p.message("The expert hands you a letter");
					p.getInventory().replace(1175, 1173);
					break;
				case 1232: // unidentified liquid
					playerTalk(p, n, "Do you know what this is ?");
					npcTalk(p, n, "Where did you get this ?");
					playerTalk(p, n, "From one of the barrels at the digsite");
					npcTalk(p, n, "This is a dangerous liquid called nitroglycerin",
						"Be careful how you handle it");
					p.getInventory().replace(1232, 1161);
					break;
				case 1161: // nitroglycerin
					playerTalk(p, n, "Can you tell me any more about this ?");
					npcTalk(p, n, "nitroglycerin...this is a dangerous substance",
						"This is normally mixed with other chemicals",
						"To produce a potent compound...",
						"Be sure not to drop it!",
						"That stuff is highly volatile...");
					break;
				case 1171: // unidentified powder
					playerTalk(p, n, "Do you know what this powder is ?");
					npcTalk(p, n, "Really you do find the most unusual items",
						"I know what this is...",
						"It's called ammonium nitrate - A strong chemical",
						"Why you want this i'll never know...");
					p.getInventory().replace(1171, 1160);
					break;
				case 1178: // mixed chemicals
					playerTalk(p, n, "Hey, look at this");
					npcTalk(p, n, "Hmmm, that looks dangerous...",
						"Handle it carefully and don't drop it!");
					break;
				case 1176: // explosive compound
					playerTalk(p, n, "What do you think about this ?");
					npcTalk(p, n, "What have you concocted now ?",
						"Just be careful when playing with chemicals...");
					break;
				case 1180: // mixed chemicals 2
					playerTalk(p, n, "See what I have done with the compound now");
					npcTalk(p, n, "Seriously, I think you have a death wish!",
						"What on earth are you going to do with that stuff ?");
					playerTalk(p, n, "I'll find a use for it");
					break;
				case 1174: // stone tablet - QUEST COMPLETION!!
					if (p.getQuestStage(Constants.Quests.DIGSITE) == -1) {
						npcTalk(p, n, "I don't need another tablet",
							"One is enough thank you!");
						return;
					}
					if (p.getQuestStage(Constants.Quests.DIGSITE) == 6) {
						playerTalk(p, n, "I found this in a hidden cavern beneath the digsite");
						removeItem(p, 1174, 1);
						npcTalk(p, n, "Incredible!");
						playerTalk(p, n, "There is an altar down there",
							"The place is crawling with skeletons!");
						npcTalk(p, n, "Yuck!",
							"This is an amazing discovery!",
							"All this while we were convinced...",
							"That no other race had lived here",
							"It seems the followers of Saradomin",
							"Have tried to cover up the evidence of the zaros altar",
							"This whole city must have been built over it!",
							"Thanks for your help",
							"Your sharp eyes have spotted what many have missed...",
							"Here, take this as your reward");
						p.message("The expert gives you 2 gold bars as payment");
						addItem(p, 172, 2);
						p.sendQuestComplete(Constants.Quests.DIGSITE);
					}
					break;
				case 1151: //belt buckle
					playerTalk(p, n, "Have a look at this unusual item");
					npcTalk(p, n, "Let me see..",
						"This is a belt buckle",
						"I should imagine it came from a guard");
					break;
				case 20: //bones
					playerTalk(p, n, "Have a look at these bones");
					npcTalk(p, n, "Ah yes, a fine bone example",
						"No noticeable fractures, and in good condition",
						"There are common cow bones however",
						"They have no archaeological value");
					break;
				case 1165: //broken arrow
					playerTalk(p, n, "Have a look at this arrow");
					npcTalk(p, n, "No doubt this arrow was shot by a strong warrior",
						"It's split in half!",
						"It is not a valuable object though...");
					break;
				case 1170: //broken glass
					playerTalk(p, n, "Have a look at this glass");
					npcTalk(p, n, "Hey you should be careful of that",
						"It might cut your fingers, throw it away!");
					break;
				case 1167: //broken staff
					playerTalk(p, n, "Have a look at this staff");
					npcTalk(p, n, "Look at this...interesting",
						"This appears to belong to a cleric of some kind",
						"Certainly not a follower of saradomin however...",
						"I wonder if there was another civilization before the saradominists ?");
					break;
				case 1166: //buttons
					playerTalk(p, n, "I found these buttons");
					npcTalk(p, n, "Let's have a look",
						"Ah, I think these are from the nobility",
						"Perhaps a royal servant ?",
						"Not valuable but an unusual find for this area");
					break;
				case 1169: //ceramic remains
					playerTalk(p, n, "I found some potery pieces");
					npcTalk(p, n, "Yes many parts are discovered",
						"The inhabitants of these parts were great potters...");
					playerTalk(p, n, "You mean they were good at using potions ?");
					npcTalk(p, n, "No no silly - they were known for their skill with clay");
					break;
				case 1157: //damaged armour
					playerTalk(p, n, "I found some old armour");
					npcTalk(p, n, "Hmm...unusual",
						"This armour dosen't seem to match with the other finds",
						"keep looking, this could be evidence of an older civilization!");
					break;
				case 1158: //damage armour body
					playerTalk(p, n, "I found some armour");
					npcTalk(p, n, "It looks like the wearer of this fought a mighty battle");
					break;
				case 39: //needle
					playerTalk(p, n, "I found a needle");
					npcTalk(p, n, "Hmm yes, I wondered why this race were so well dressed!",
						"It looks like they had a mastery of needlework");
					break;
				case 1155: //old boot
					playerTalk(p, n, "Have a look at this");
					npcTalk(p, n, "Ah yes, an old boot",
						"Not really an ancient artifact is it?");
					break;
				case 1162: //old tooth
					playerTalk(p, n, "Hey look at this");
					npcTalk(p, n, "Oh, an old tooth",
						"..It looks like it has come from a mighty being");
					break;
				case 1173: //scroll (permission)
					npcTalk(p, n, "There's no point in giving me this back!");
					break;
				case 1117:
				case 1148:
				case 1149: //rock samples
					playerTalk(p, n, "Have a look at this rock");
					npcTalk(p, n, "This rock is not naturally formed",
						"It looks like it might belong to someone...");
					break;
				case 801: //rotten apples
					playerTalk(p, n, "I found these...");
					npcTalk(p, n, "Ew! throw them away this instant!");
					break;
				case 1159: //rusty sword
					playerTalk(p, n, "I found an old sword");
					npcTalk(p, n, "Oh, its very rusty isn't it ?",
						"I'm not sure this sword belongs here",
						"It looks very out of place...");
					break;
				case 1168: //vase
					playerTalk(p, n, "I found a vase");
					npcTalk(p, n, "Ah yes these are commonly found in these parts",
						"Not a valuable item");
					break;
				default:
					p.message("Nothing interesting happens");
					break;
			}
		}
	}
}
