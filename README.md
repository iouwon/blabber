Unattended coding demo called blabber console app that mimics social media messaging

I have taken the approach of immutability over mutabilty to approach referential transparency and an ability to reason about the code without any unexpected side effects. I have tried to do this without sacrificing performance where possible. To this end I found guava quite unuseful as an immutable collections library and preferred the out of the box facilities that java provided. Scala's immutable collection library stands head and shoulders above these.

Thought about using TotallyLazy but seemed like a lot of project just to be a bit syntactically nicer for this task. Using scala collections in java is... well... less said the better.