# AI-Driven Automated Unit Test Generation via KDM Semantic Bridge

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Research: EMI-LASTIMI](https://img.shields.io/badge/Research-EMI--LASTIMI-blue)](https://www.emi.ac.ma/)

This repository contains the official implementation of the research project: **"An LLM-as-a-Judge Approach to Optimizing KDM-Based Unit
Tests Generation"** at the **LASTIMI Laboratory** (Laboratoire d’Analyse des Systèmes, de Traitement de l’Information et du Management Industriel), École Mohammadia d'Ingénieurs (EMI), Rabat.

## 📌 Project Overview
Traditional automated test generation tools often struggle with code semantics and architectural dependencies. This project introduces a **"Semantic Bridge"** that elevates complex KDM/XMI metadata into a denoised Focal Context. This context is then processed by a **Multi-LLM Consensus pipeline** (using Claude, DeepSeek, and Gemini via Ollama) to achieve high-precision unit tests.

### 🚀 Key Performance Metrics
* **Compilability:** 99.3%
* **Test Coverage:** 100% (on the BankSys benchmark)
* **Architecture:** Model-Driven (KDM) + Multi-LLM Orchestration

---

## 🏗 System Architecture
The pipeline consists of two main phases:


1.  **Transformation (The Bridge):** A Python engine that converts verbose XMI metadata into a structured JSON Focal Context, resolving architectural pointers.
2.  **Generation & Validation:** A Multi-LLM setup serving as a "Judge" to verify and refine test suites through a consensus logic.



---

## 🛠 Installation & Setup

### Prerequisites
* **Python 3.10+**
* **Ollama** (for local LLM orchestration)
* **Java 17+ & Maven** (to run the generated BankSys tests)
* **Eclipse MoDisco** (for the initial XMI extraction)

### Quick Start
1. **Clone the repository:**
   ```bash
   git clone [https://github.com/SalahEddineELMENYIY/KDM-Semantic-Bridge-Unit-Tests.git](https://github.com/SalahEddineELMENYIY/KDM-Semantic-Bridge-Unit-Tests.git)
   cd KDM-Semantic-Bridge-Unit-Tests
   '''
2. **Install dependencies::**
   ```bash
   pip install -r requirements.txt
   '''
3. **Configure Ollama:**
   Ensure Ollama is running, then pull the models used in the Multi-LLM Judge:
   ```bash
   ollama pull gemma
   ollama pull deepseek-v3
   '''

## 📂 Repository Structure

The project is organized to facilitate the transition from raw architectural models to validated test suites:

```text
.
├── code/                          # Target System Source Code (BankSys)
│   ├── IBankService.java          # Service Interface
│   ├── InsolventException.java    # Custom Exception
│   ├── Main.java                  # Application Entry Point
│   └── SavingsAccount.java        # Core Business Logic
├── BankSystem_new_kdm.xmi         # Raw KDM model extracted via MoDisco
├── kdm_data_focal_context.json    # Denoised Semantic Bridge output
├── promptGenKDM-New.ipynb         # Multi-LLM Orchestration & Prompting logic
├── tests.json                     # Final validated test suite (Consensus output)
└── generatedTests.json            # Initial test candidates
