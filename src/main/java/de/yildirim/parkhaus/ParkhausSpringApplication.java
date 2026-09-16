package de.yildirim.parkhaus;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Einstiegspunkt der Anwendung.
 *
 * <p>{@code @SpringBootApplication} setzt den Wurzelpunkt für die Komponentensuche:
 * alle Pakete unterhalb von {@code de.yildirim.parkhaus} werden durchsucht. Deshalb
 * liegt diese Klasse ganz oben und nicht in einem Unterpaket.
 *
 * <p>Den Aufbau der Anwendung beschreibt {@code config.ParkhausConfig}; hier steht
 * nur, was vor dem Hochfahren feststehen muss.
 *
 * @author      Ümit Yildirim <hopes61@icloud.com>
 * @copyright   Copyright (c) 2026 Ümit Yildirim. Alle Rechte vorbehalten.
 * @license     Diese Datei darf nicht ohne Zustimmung des Autors weitergegeben oder verändert werden.
 */

@SpringBootApplication
public class ParkhausSpringApplication
{
	public static void main(String[] args)
	{
		SpringApplication app = new SpringApplication(ParkhausSpringApplication.class);

		// v1.0 hat eine Swing-Oberfläche. Boot setzt sonst java.awt.headless=true,
		// weil es eine Serveranwendung ohne Bildschirm unterstellt — jedes new JFrame()
		// scheitert dann mit HeadlessException. Muss VOR run() stehen: Boot wertet
		// das Feld ganz am Anfang von run() aus, bevor die application.properties
		// gebunden werden. Deshalb wirkt hier keine Eigenschaft, nur der Setter.
		app.setHeadless(false);
		app.run(args);
	}


}
