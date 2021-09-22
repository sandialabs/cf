---
layout: default
title: CF Documentation
---

Welcome to the documentation site of the Credibility Framework (CF)!

## Product Perspective

Engineering credibility has been defined in many ways over the past two decades. Fundamentally credibility is a set of tools and processes for decision makers to understand why (or whether) they should trust the ModSim model to make decisions as well as develop a sense risk associated with making such decisions.

The Credibility Framework is an initial implementation of the credibility tools for mechanical engineers. It is largely a stand-alone Eclipse application intended to work seamlessly with the analysis data management back-end to identify whether artifacts referenced by the credibility evidence package are under configuration control by the Analysis Data Management layer.

## Product Scope

The Credibility Framework shall provide intuitive and coherent environment for ModSim teams to:
- define a Credibility Framework project to track evolution the credibility evidence associated with a particular ModSim task
- communicate credibility of computational simulation models
- provide and maintain links to configuration controlled artifacts constituting the evidence package
- tag the evidence package at significant stages of the ModSim program
- quantitatively assess the state of credibility if they choose
- support internal and external peer reviews
- conduct a historical review of credibility throughout the life of the ModSim project including understanding diverse input from different team and review panel members

It is important that all behavior of the Credibility Framework are configurable by non-programmers without having to touch source code.

## Product Functions

On the high-level Credibility framework implements a document management system and a guided information harvesting and ranking tool providing members of the ModSim team and reviewers to collect, disseminate and optionally rank groups of attributes of the ModSim activity. 

The Credibility Framework is not a physical experiment management of computational model execution platform, but it provides links to artifacts from those processes managed and executed by the workflow engine. 

For example, definition, execution and post processing of a UQ study on a complex analysis workflow is managed by the workflow engine, the Dakota wizard and results visualization tools. 

Consequently, the Credibility Framework under the Uncertainty Quantification sub element of PCMM maintains links to instances of the UQ studies and their documentation intended to provide evidence for making statements about margin uncertainties.

## Acronyms:
- `PIRT`: `P`henomena `I`dentification `R`anking `T`able
- `QoI`: `Q`uantity `o`f `I`nterest
- `PCMM`: `P`redictive `C`apability `M`aturity `M`odel

## Contacts
