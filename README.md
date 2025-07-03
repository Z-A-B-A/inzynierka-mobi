# Management System for Controlled Habitat Environments

This project is an Internet of Things (IoT) solution designed for fishkeeping and terraristics, providing a comprehensive system for monitoring and managing environmental parameters in controlled habitats.

## Overview

The system is composed of three main components:

- **Measuring and Controlling Device:**  
  Built on a Raspberry Pi microcomputer, this device collects real-time environmental data (such as temperature, humidity, water quality, etc.) using integrated sensors. Based on both the collected readings and user-defined configurations, the device controls actuators to maintain optimal habitat conditions.

- **Central Server:**  
  Acting as the system’s core communication hub, the server manages data storage, notification handling, and configuration distribution. All sensor data is saved in a database, and user alerts are efficiently managed. The server also pushes user-defined setups and parameters to the edge devices.

- **User Interfaces:**  
  The system offers both a mobile application and a website, serving as graphical user interfaces. Through these interfaces, users can:
    - View the current status of all connected devices
    - Add and configure new devices
    - Set and adjust habitat parameters

## Features

- Integrated monitoring and management of habitat conditions
- Real-time data collection and control via IoT devices
- Centralized server for data syncing, notifications, and management
- User-friendly web and mobile apps for full system control

## Technology

- **Kotlin:** Main programming language (for mobile app)
- **Raspberry Pi:** Edge computing device for measurement and control
- **Web technologies:** For the management website
- **Go** For API server

---

[![Watch the demo]](https://youtu.be/c9P8hOdmjLQ)
