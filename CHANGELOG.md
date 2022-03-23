# Changelog
All notable changes to this project will be documented in this file.

## [1.0.1]

### Added
- add Java 11 as minimal requirement
- add drag'n'drop multi selection support and reordering for PIRT, QoI, System Requirement, Decision, Uncertainty and PCMM Evidence trees
- keep the drag'n'drop order in the report
- automatically inline images into the report
- add concurrency support prototype as a developer option: 
It embeds the CF plugin compatible with local and web projects, and a web application to manage web projects.
Only ModSim Intended Purpose feature is activated to demonstrate the concurrency project feasibility and define the requirements.
With the creation wizards, it is possible to create or to connect to an existing project.
No user rights are required and user authentication is just saving user name.
- rich text fields (html) represented in the generated document
- report: generate PIRT pages in landscape mode
- add native browser spell checker for Richtext
- add figure captions input for inserted images
- add pre-defined categories under uncertainty inventory
- add update support for uncertainty configuration import
- remember the PCMM evidence last folder
- order PCMM evidence arbitrarily (simple and multiple drag'n'drop repositioning)
- delete evidence with DEL key

### Changed
- "Credibility Setup" with Simplified and Advanced pages
- simplify report setup (remove python command)
- enhance setEnv file error management
- open .cf file with a locked temporary data folder
- create a new data folder with timestamp if the default one is locked
- always save the data folder without timestamp (if the default data folder is locked)
- lock HSQLDB files to allow only one connection
- delete migration cancelled warning message
- change CF plugin incompatible version message
- delete temp folder via Eclipse Resources plugin first
- synchronize methods around entity manager and database closure
- set .cftmp folder as derived file (without workspace history and versioning)
- change CF file loading and check if file is recoverable before migration
- add ARG version 1.1.9 support 

### Fixed
- #FIX migration exceptions for cf files <= 0.2.0
- #FIX log exception stack trace with eclipse log mechanism
- #FIX bug during unzipping: create zip file parent directory if not exists
- #FIX keep the PCMM evidence user selection for tree expanded items
- #FIX opening PCMM without QoI warning launches multiple popup
- #FIX PCMM wheel not displayed on Home view
- #FIX extend PCMM evidence dialog width
- #FIX Minitools loading error (for multilevel tools)
- #FIX bug on Report View loading (word inlining)
- #FIX expand element/subelement for new added PCMM evidence
- #FIX keep PCMM evidence expanded items selection
- #FIX force guidance trees font color to white on dark blue
- #FIX PCMM_schema-With_Subelements_3_Levels-Assessment seems wrong
- #FIX arg null value not interpreted
- #FIX report generation issues related to html support
- #FIX richtext formatting and text not saved
- #FIX special characters in richtext editors
- #FIX richtext not formatted for Uncertainty and Analyst Decision
- #FIX bug on PCMM Evidence value truncated
- #FIX Uncertainty migration bug
- #FIX reordering issue for lists >10 items
- #FIX handle access denied exception to .cftmp folder
- #FIX connection not closed with a messy .cftmp folder
- #FIX delete user message if the existing database is not recoverable
- #FIX change the evidence name with the updated path
- #FIX PCMM - Allow editing evidence text field
- #FIX nebula plugin incompatibility with SAW 2.5.4

### Security
- log4j vulnerability update to logback 1.2.9


## [1.0.0]
The official first release. CF is an opensource project based on EPL 2.0 licence.

See wiki page at https://gitlab.com/CredibilityFramework/cf/-/wikis/1.0.0/Release-1.0.0.
