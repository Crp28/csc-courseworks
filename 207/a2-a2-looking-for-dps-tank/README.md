# Assignment 2 Instructions

## Overview
In this project, you will be required to design a working software application based on specifications provided (mimicking the sort of specifications you may receive from a real-life client).

## Part 1: Read the project specifications below
Large cities often have a public transit system that includes trains, trolleys, busses, etc. You will be creating a program that tracks and calculates fares for anyone who uses a travel card (called a "cardholder") to enter and exit the system.

Your program should work with a transit network that contains only above-ground buses and underground trains (i.e. "subways").

Fares are calculated using a combination of time and distance, as measured in minutes and number of stops/stations respectively. (For a definition of "stop" and "station", see the section below called "Transit System".)

Your system will be used by cardholders and transit staff (admin users).

It is a good idea not to hard code values into your program so that it can be used in more than one city, where fares, transit maps, caps, etc. may differ from each other.

### Fares:
Each bus ride costs $2. In other words, $2 is deducted from the travel card's balance every time someone enters a bus. Unlike the TTC/Miway, this system does not have transfers - fares are deducted from the cards balance every time the cardholder enters a vehicle.

Subway rides cost $0.50 for every subway station reached by the rider. This will include every station except for the first one. For example, entering at Station A, riding through Station B, and then exiting from Station C will cost $1 because the user travelled to B and to C, which is two stations. Entering a subway station and immediately exiting is free.

Fares are deducted from the card balance every time a cardholder exits a station. Nothing is deducted upon entry into a station. This is opposite to the way money is deducted upon entry to (not exit from) a bus.

You can assume that there is a different input when a card taps to enter a station and to exit a station. In other words, the input will tell you whether a person is entering or exiting a station/bus.

It is possible for someone to illegally enter or exit a station without tapping or simply forget to tap on the way out of a bus. Alternatively, the hardware could malfunction due to a power outage, causing non-standard tapping sequences. It is up to your group to decide how you want to handle these pathological cases (e.g., someone appears to tap out of a station without ever tapping into the system, etc.). It is up to you to design a way of handling these situations that will be appealing to your customer (the government who runs the transit system).

### Transit system:
Points on the subway line are called "stations" and points along a bus route are called "stops". When a cardholder "taps out" (exits) a bus, the time and location name of the stop is recorded, to check if the next entrance is at the same location. If not, the next entrance is considered to be the beginning of a new trip. This is relevant when calculating caps (see Travel Restrictions).

Bus routes all have a starting stop and an ending stop, and do not return to the same location. Bus routes all include at least one subway station that it stops at. This information may or may not be useful for your design. You have to decide.

You can assume that there is only one subway line and that it does not intersect itself.

You can also assume that all bus routes include at least one subway station and each bus route does not intersect itself.

A continuous trip through the system involves entering and exiting subways and buses at points where their routes intersect.

### Travel Restrictions:
The system keeps track of the times when a card taps into and out of each subway station and/or bus.

Individual trips are capped at $6. In other words, all entrances and exits from the transit system by an individual cardholder within a 2 hour period are deducted from the card's balance up to a maximum of $6, after which travel is free, as long as all entrances are within 2 hours of the time of the initial entrance that began the trip.

If a person rides the subway for three hours without exiting, the most they can be charged is $6. However, if they exit the subway after riding for three hours and then enter a bus, they will be charged separately for the bus because they entered it more than two hours after beginning their trip.

The two hour cap does not apply to disjointed trips. For example, if a cardholder exits from a subway station and then enters a bus that does not stop at that station, the bus entrance is not counted towards the last trip. Instead, a new 2-hour period has started for this cardholder.

### Cardholders:
Each transit user has an account that stores their name, email address, and links to any and all travel cards.

Users can change their name, but not their email address. They can also add and remove as many travel cards as they want.

Cards each have an associated balance that must stay in positive numbers (or $0) with one exception: If the next entrance (into a subway or bus) will move your balance from a positive to a negative number, the system will let you pay, but then prevent any further entrances until you add to your card balance.

Cardholders can add $10, $20, $50 to their card balance at a time.

All new cards start with a $19 balance.

Users can also view their three most recent trips, suspend a stolen card's activity, and track their average transit cost per month.

### Admin Users:
The people who run the transit system should be able to compare overall revenue to their operating costs. They will do this by comparing all fares collected each day with the total number of stops/stations reached by all cardholders. The daily report can be printed to the screen or a file -- your choice (i.e. you can choose how you want to show the report).

Note that in the future, your client may ask you to keep more detailed statistics, so your system should be extensible in that direction.

You can decide how you want to handle the separation from one day to the next. This can involve input from the user. Alternatively, your system can assume that the program runs continuously during the day and then is closed at the end of each day. This last option requires you to have a method of input for ending execution.

### System Boundaries:
Each entry and exit of a card from/to a station or bus is considered to be input into your program.

Transit user interactions with their account involves both input and output (viewing/modifying their balance, adding/removing cards, changing their name, viewing their recent trips, etc.)

Any messages to the user are considered to be output. For example, telling a user that they cannot enter because their balance is already negative. On the TTC, this looks like a red X on the screen when a cardholder attempts to tap into a station or onto a bus.

Any confirmation messages to the user is considered to be output. For example confirming that money has been added to a card's balance is considered to be output.

All requests from admin user(s) are input. The resulting information displayed to the admin user(s) is output.

### Data input structure:

1. You can have a text file called events.txt that contains one line for each individual input. For example, you can have a line that says "1204 enters St. George Station". Alternatively, the same input could be formatted as "St. George Station entry Card 1204". Or you could decide to include more or less information for that particular input. You get to decide on the format for each line of events.txt

2. Alternatively, you may have a user interface instead of events.txt, so that all input will come from the user.

### Data output structure:

All output can be written to the screen using text. For example, your program can have println statements saying things like "$20 has been added to the balance of card 12345670987".

## Part 2: Create a product backlog
See scrum_data.txt for a link to the spreadsheet template you should use for this.

Your product backlog must list all the tasks you need to do for this project, based on the specifications above. Part of software design in a real world setting is identifying development tasks based on specifications that might be imprecise, like some of the information provided to you in Part 1. In your copy of the spreadsheet we provided, write down each "user story" (requirement from the specifications that you need to complete), with an "estimation" and a "priority" ranking for each story. In the included file "scrum_data.txt" in this repository, copy/paste the link to your product backlog (follow the instructions in the "scrum_data.txt" file to do this), then add and commit the scrum_data file. Do this before doing anything further in this assignment.

Note: Product backlogs change over time. Don't expect to get everything right the first time.

More information about each entry in the backlog:
1. User story: The user stories should specify the user which the task is for (in this case, cardholder or admin user), and what it is that the user wants to do (follow the example format in the given spreadsheet)
2. Estimation: The estimation should be how much effort you estimate a task will take (for particularly large tasks, break it up into smaller 'sub-tasks' -- you can make their IDs be in the format 1.1, 1.2, ...). Within the Scrum Framework the estimation is usually not actual time estimates - a more abstracted metric to quantify effort is used. For this project, we want you to use sizes like XS (really tiny task), S (small task), M (medium task), L (large task), or XL (very large task).
3. Priority: Rank each user story to be either "low", "medium" or "high" priority

## Part 3: Do a code sprint
Go through the product backlog and choose which tasks will be completed in the current sprint. Drag the chosen tasks in your spreadsheet from the "unassigned to sprint" heading to be under the current sprint's heading instead. Add a "Task Owner" -- the team member who will be in charge of that task for this sprint, and a "Status" as "In progress" (change this to "Complete" once a task is done).

We suggest one-week long code sprints. So, for this assignment, as a simulation of a Scrum project, you will have about two code sprints.

We realize you have other courses and don't expect you to hold daily scrum meetings. However, aim to have a scrum meeting at least every 2-3 days or so. Remember, attendance at these meetings is mandatory.

### Getting started with coding

To get started with the code, in Eclipse choose File > Open Projects from File System, and then navigate to the your a2 repository you cloned from Github classroom. All your .java files should be created within the "src" directory inside a2. Feel free to make your own packages within "src" to organize your files.

### Git Workflow during code sprint

We will be learning to use a few new git commands through this assignment -- git branch, git checkout, and git merge. Read this nice tutorial for an overview and some examples of each of these commands (use the sidebar to navigate through the "Using branches" section): https://www.atlassian.com/git/tutorials/using-branches

For this project, each task will have its own "branch" within your repository where it is coded, and one person in your team (the "team manager") can be in charge of merging each branch to the "master" branch as tasks get completed. (Note: You do not need to make new branches for sub-tasks of each user story task if you have them (such as 3.1.x))

So, anytime you begin working on a new task, you should do the following:

- git pull: this is very important to do before you start coding! This will pull the latest history for this branch from the remote (origin) and merge with the latest commit. Do this on a clean workspace (before you make any local changes), otherwise things get complicated.

- git branch NEW_BRANCH where NEW_BRANCH is an identifying name for the task this branch will be dedicated to; this command branches off the latest version of working code from the remote

- git checkout NEW_BRANCH: this command switches your working area to the branch that you want to checkout. Do NOT forget to do this after creating the new branch! or else you will still remain in master. (Note: You can type 'git branch' without anything after the command to get a list of all the available branches, and the current branch that you are working on will be highlighted).

Then, modify the files relevant to the task for this branch. Once you are done:

git add --all .
git commit -m "COMMIT MESSAGE"
git push (you may have to do git push --set-upstream origin NEW_BRANCH to configure the new branch the first time you're pushing it)

Once you are done working on a branch and you want to merge the work you did back to master:

git checkout master
git pull  (gets all latest development from master branch)
git merge NEW_BRANCH

If there are conflicts: resolve conflicts by opening the files and keeping what you want, deleting what you don't want, and then again: add, commit and push.

Now, on Github you need to submit a pull request. Navigate to your branch and click "Compare & Pull request" and let your team manager know that you made some changes that need to be pulled into master. (See the video linked below for a demo of this.)

See this video for an example of this workflow:
https://www.youtube.com/watch?v=oFYyTZwMyAg&ab_channel=LearnCode.academy

*Note: If you want to use different tools like Github Desktop or Eclipse's git menu instead of directly using the git commands like we suggested above, that's okay, as long as you are still following the same workflow.*

## Part 4: Repeat parts 2-3
Revisit the product backlog, updating it as necessary, and do code sprints. Repeat until the deadline.

## Part 5 (OPTIONAL): Adding bonus features

For bonus marks (up to 5% extra), you can add your own extra features to make your program unique and more marketable. e.g. Having a GUI interface to input events rather than using events.txt would count as an example of a 'bonus feature'. Your extra features could also be statistical in nature, letting the user and admin user view useful information e.g. past information about trips, balances, revenue, etc. You can assume that the admin will archive any information that is too old to show up in your program’s statistical display. You can decide how old is too old. 

NOTE: Do NOT hand in half-finished bonus features - if you want us to see your attempt at completing one, you can describe this in your FEATURES.txt file or comment out the relevant, incomplete code with a note stating this was an attempt at a bonus feature. Your submitted program must be fully functional and able to run when you hand it in to avoid getting a grade of 0 on the "functionality" portion of this assignment. If we can't run your code, you will get no marks for "functionality".

## Part 6 (IMPORTANT): Tell us about your code and how to use your software

We will mainly use this file to grade your code. This means, if you do NOT complete these files, you will receive a grade of 0 in this assignment.

FEATURES.TXT
Complete the file FEATURES.txt that explains how to run your code, and lists and describes all the features of your project, and which part of the provided specifications they fulfill. The description should mention any important design decisions you made in coding that feature (e.g. if you used inheritance, or a design pattern, etc. clearly let us know in this file, and briefly explain why you made this decision). If you have any bonus features you added in, clearly include this here with a title "Bonus feature".

This file should instruct the marker on how to run your program, what format to use for each type of input into events.txt, and any other information we need to set up and run your code. If you use other configuration files besides events.txt please include detailed instructions so that we know how we can and cannot modify those files in order to get your program to run.

## Notes

### Citing Code:
If you use any code you find, cite it according to the format described in the "Examples of citing code sources" section of the "Writing Code" page of the MIT Academic Integrity handbook here: https://integrity.mit.edu/handbook/writing-code

### Some other helpful git commands:

Check the current status of all the files in the repository:
git status

Tools for undo-ing all your local changes:
git reset --hard: put tracked files back as they were
git clean -fd: remove untracked files

### Grading:

Each group's project should have similar functionality, even if each design is different from one group to the next. Try to make your design encapsulated so that a change to one part of the program does not have much impact on other parts of the program. Consider each of the SOLID principles (https://www.baeldung.com/solid-principles) when making design decisions. Likewise, we will be discussing design patterns during the next few lectures. Keep in mind if any of the design patterns can improve your design.

There is more than one possible design that will work well with this project. You only have to develop one of these designs. If you are having trouble making major decisions about how the overall design of your project should look, feel free to ask your TA during lab, attend office hours as a group or book an appointment with an instructor to discuss it. There may also be clarifications posted to the message board (Piazza). Make sure that at least one group member is monitoring Piazza on any given day.

This is a course called Software Design. Therefore, most of your mark will come from your design.

Here is a *tentative* grading scheme:
- Functionality (based on all your working features): ~35\%
- Code Architecture (use of object-oriented programming principles and design patterns, and a clear discussion of these design decisions in FEATURES.txt): ~25\%
- Code Style (no repetitive code, overall code is clean and easy to read): ~25\%
- Documentation (complete, well-written Javadocs): ~5\%
- Use of Scrum (based on your product backlog): ~5\%
- Use of git (following the workflow detailed in this handout, using properly named branches for each task, having useful commit messages, having multiple commits throughout the assignment work period rather than all at the end): ~5\%

#### Extensibility:
Keep in mind that your imaginary client may ask to add further requirements over the next month or so. The requests might involve expanding your software to include more features or handle more inputs. Be sure to design your code with this in mind.

In real life, you would be able to ask a contact with the municipal government for further clarification regarding the software they want from you. For the purposes of this project, you can direct such questions to the discussion board. Any response from instructors is to be taken as the government's response. You are also invited to do your own research regarding transit systems. For example, what does “tap into” and “tap out of” a station really mean?

## What To Submit
As you work, regularly commit and push your changes. We will be checking the git logs to make sure everyone is making a significant contribution. Try to make your last changes to the code at least one day before the due date. That will give you enough time to finalize your code, update your FEATURES.txt file and check that your program still works after everyone's work has been merged for the last time.

We will be looking for:
- A completely functional program that meets the above specifications
- events.txt (if relevant)
- scrum_data.txt
- FEATURES.txt
- Any other configuration files that you need for us to run your code
