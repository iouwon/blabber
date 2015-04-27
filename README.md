Unattended coding demo called blabber console app that mimics social media messaging

Run the application with gradle (I'm using version 2.3) from the project root using "gradle -q run"

Users submit commands to the application. There are four commands. commands always start with the user’s name.<br/>
[user name] -> [message]            //posts a message from the user to their timeline<br/>
[user name]                         //publishes the user's timeline<br/>
[user name] follows [another user]  //first user follows the second user<br/>
[user name] wall                    //publishes the user's posts interspersed in time order with all the posts of those they're following<br/>

I have taken the approach of immutability over mutable state where I can to approach an app with referential transparency and an ability to reason about the code without any unexpected side effects. I have tried to do this without sacrificing performance where possible. To this end I found guava quite unuseful as an immutable collections library and preferred the out of the box facilities that java provided. Scala's immutable collection library stands head and shoulders above these.

The only place where things are mutable are in the command loop and the main class. In the command loop a recursive approach would have worked but this is limited to a number of calls before the stack overflows due to a lack of tail recursion in java. Trampolines would work here but I feel in this scenario they obscure more than they help. The main class has a setter that is only callable from a test context.

Thought about using TotallyLazy but seemed like a lot of project just to be a bit syntactically nicer for this task. Using scala collections in java is... well... less said the better.

The wall amalgamates all the posts from all the users being followed by the owner of the wall. To retrieve these posts in a timely fashion and reduce the processing overhead each wall should keep a reference to the timelines of all those followed. Then for retrieval, the posts from each timeline should be retrieved one by one in order across all timelines. This means that some of the posts will be iterated over multiple times as the algorithm searches for the next Post to retrieve. To minimise the processing of multiple calls to the wall you can cache the current merged timelines, which would then be processed in the same way with any new timelines of the subsequently followed. If a user followed significant numbers without a wall request then the first time could be costly.

A quicker alternative for retrieving the messages would be for the wall to merge the posts of all the timelines of the followed but new posts would be placed onto the timeline of the user and onto the walls of any followers, in a publish-subscribe/reactive messaging pattern. With many followers this would result in a massive increase in messages and could flood a system, if there was a lot of posting activity. It would also mean consuming significant amounts of memory to hold in place all the messages previously sent including the copies held by all the followers.

Given the possible volumes involved and the need to keep processing and messaging to a minimum I think keeping one copy of the timeline data and merging on request would be best and would be made performant in high volume scenarios by simply time-slicing the timelines and merging only the data returned in the time slices, as well as filtering followers out or adding them in (black vs white list) to the results, as users could only practically consume a small number of posts.

Wanted to do a recursive console but due to a lack of tail recursion in java didn't feel this was a good idea but I use a null return value from the main command loop to signify an exit and return a version of the immutable service object when processing the regular commands.

I haven't paid any attention to timezones here and have assumed the default timezone of the system is suitable.