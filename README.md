#Unhandled Inconsistencies:

**Some pages go by multiple names, so some people have more than 2 parents**

Example: https://en.wikipedia.org/wiki/Margaret_of_Germany redirects so Frederick I, Margrave of Meissen has three parents as it counts both URLs as different people

Solution: Hopefully implement a thing to get the redirected URL. Alternatively, pretend that the people are real

---
**Some pages have spouse names in the children section.**

Example: https://en.wikipedia.org/wiki/Jan_Kostka has the name Zofia in the Issue section

Solution: Only count it as a child if the href is the only thing in that text line

---

**Some children don't actually change the page**

Example: https://en.wikipedia.org/wiki/Prince_Tomislav_of_Yugoslavia

Solution: Hopefully implement a thing to get the redirected URL. Alternatively, ignore anyone whose child's page has the same name as their previous page

---

**Some parents link to a location instead of a name**

Example: https://en.wikipedia.org/wiki/Vakhtang_V_of_Kartli and click on Mother

Solution: Look at the page html and search for an indicator that the page is a person and not a place

---

Run with Java 17, Maven, IntelliJ, 
