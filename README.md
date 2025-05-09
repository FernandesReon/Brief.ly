# Brief.ly

## Overview
Brief.ly is a Spring-based web application that enables users to shorten URLs quickly and efficiently. It features a 
secure user authentication system to ensure data privacy and offers real-time analytics, allowing users to track usage 
metrics at any moment.

## Features
Currently, Brief.ly overall comes with features like
- User Authentication and Authorization (Spring Security, JWT)
- Role-based Access (User, Admin)
- Persistence for Original Url (MySQL)
- Analytical information for following: 1. Specific Url, 2. For a given TimeStamp
- Caching for seamless low latency data fetching (Redis) (Yet to be implemented)