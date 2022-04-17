#Unhandled Inconsistencies:
**Some pages redirect back to itself which makes it cyclic.**

Example: https://en.wikipedia.org/wiki/Margaret_of_Germany redirects so Frederick I, Margrave of Meissen has three parents as it counts both URLs as different people

Solution:

---
**Some pages go by multiple names, so some people have more than 2 parents**

Example:

Solution: Hopefully implement a thing to get the redirected URL. Seems difficult, so we can just pretend that the people are real maybe?

---
**Some pages have spouse names in the children section.**

Example: https://en.wikipedia.org/wiki/Jan_Kostka has the name Zofia in the Issue section

Solution: Add anyone with this inconsistency to the isLinkMalformed method

---
**Some children don't actually change the page**

Example: https://en.wikipedia.org/wiki/Prince_Tomislav_of_Yugoslavia

Solution: 

**Some parents link to a location instead of a name**

Example: https://en.wikipedia.org/wiki/Vakhtang_V_of_Kartli and click on Mother

Solution: Add any locations with this inconsistency to the isLinkMalformed method

---


Run with Java 17, Maven, IntelliJ, 
