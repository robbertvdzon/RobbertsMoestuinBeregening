import 'dart:async';
import 'dart:html' as html;

import 'package:cloud_firestore/cloud_firestore.dart';
import 'package:firebase_auth/firebase_auth.dart';
import 'package:firebase_core/firebase_core.dart';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

import 'firebase_options.dart';
import 'model.dart';
import 'schedules.dart';
import 'viewModelProvider.dart';

void main() async {
  WidgetsFlutterBinding.ensureInitialized();
  await Firebase.initializeApp(
    options: DefaultFirebaseOptions.currentPlatform,
  );
  runApp(
    ChangeNotifierProvider(
      create: (context) => BeregeningDataProvider(),
      child: const MyApp(),
    ),
  );
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Beregening Robbert\'s moestuin',
      theme: ThemeData(
        colorScheme: ColorScheme.fromSeed(seedColor: Colors.deepPurple),
        useMaterial3: true,
      ),
      home: AuthStateChecker(),
    );
  }
}

class AuthStateChecker extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return StreamBuilder<User?>(
      stream: FirebaseAuth.instance.authStateChanges(),
      builder: (context, snapshot) {
        if (snapshot.connectionState == ConnectionState.waiting) {
          // Toon een laadscherm terwijl de status wordt gecontroleerd
          return Center(child: CircularProgressIndicator());
        } else if (snapshot.hasData) {
          // Gebruiker is ingelogd, ga naar MyHomePage
          return MyHomePage(title: 'Beregening Robbert\'s moestuin');
        } else {
          // Gebruiker is niet ingelogd, ga naar LoginPage
          return LoginPage();
        }
      },
    );
  }
}

class MyHomePage extends StatefulWidget {
  const MyHomePage({super.key, required this.title});

  final String title;

  @override
  State<MyHomePage> createState() => _MyHomePageState();
}

class _MyHomePageState extends State<MyHomePage> {
  late Stream<String> _timeStream;

  String getTimeLeft(ViewModel? viewModel) {
    String timeLeft = "Unknown";
    final viewModelCopy = viewModel;
    if (viewModelCopy != null) {
      if (viewModelCopy.pumpStatus == PumpStatus.CLOSE) timeLeft = "Closed";
      if (viewModelCopy.pumpStatus == PumpStatus.OPEN) {
        timeLeft = calculateTimeDifference(
          viewModelCopy.pumpingEndTime.year,
          viewModelCopy.pumpingEndTime.month,
          viewModelCopy.pumpingEndTime.day,
          viewModelCopy.pumpingEndTime.hour,
          viewModelCopy.pumpingEndTime.minute,
          viewModelCopy.pumpingEndTime.second,
        );
      }
    }
    String disabledText = "";
    if (viewModelCopy?.valveStatus==ValveStatus.MOVING) disabledText = "(aan het wisselen)";
    return timeLeft+" "+disabledText;
  }

  @override
  void initState() {
    super.initState();
    _timeStream = Stream.periodic(Duration(seconds: 1), (_) => "");
    _requestBackendUpdates();

    // Luister naar veranderingen in tab-/venstervisibiliteit
    html.document.onVisibilityChange.listen((event) {
      if (html.document.visibilityState == "visible") {
        _requestBackendUpdates();
      }
    });
  }

  @override
  void dispose() {
    super.dispose();
  }

  void _requestBackendUpdates() {
    _addCommand("UPDATE_STATE");
  }

  void _addCommand(String data) async {
    setState(() {
      final _db = FirebaseFirestore.instance;
      final jsonKeyValue = <String, String>{"command": data};
      _db
          .collection('bewatering')
          .doc('commands')
          .set(jsonKeyValue, SetOptions(merge: true))
          .onError((e, _) => print("Error writing document: $e"));
    });
  }

  void _nothing() {}

  @override
  Widget build(BuildContext context) {
    final beregeningDataProvider = Provider.of<BeregeningDataProvider>(context);
    final beregeningData = beregeningDataProvider.beregeningData;

    return Scaffold(
      body: Center(
        child: Container(
          width: 1000,
          // Stel een vaste breedte in
          height: double.infinity,
          // Vul de volledige hoogte
          alignment: Alignment.center,
          decoration: BoxDecoration(
            image: DecorationImage(
                image: AssetImage('assets/beregening.png'),
                // Jouw afbeeldingsbestand
                fit: BoxFit.cover,
                // Past de afbeelding aan zodat deze de hele achtergrond bedekt
                alignment: Alignment.topCenter),
          ),
          child: Column(
            // mainAxisAlignment: MainAxisAlignment.center,
            children: <Widget>[
              Text('Laatste status update: ${beregeningData?.lastUpdate}'),
              SizedBox(height: 270),

// Controller
              Container(
                margin: EdgeInsets.all(10), // Marges rond het blok
                padding: EdgeInsets.all(15), // Binnenruimte binnen het blok
                decoration: buildBoxDecoration(),
                child: Column(
                  mainAxisSize: MainAxisSize.min, // Minimaliseer de hoogte
                  children: [
                    StreamBuilder<String>(
                      stream: _timeStream,
                      builder: (context, snapshot) {
                        if (!snapshot.hasData) {
                          return CircularProgressIndicator();
                        }
                        return Text(
                            'Timer: ${getTimeLeft(beregeningData?.viewModel)}');
                      },
                    ),
                    SizedBox(height: 10),
                    Row(
                      mainAxisAlignment: MainAxisAlignment.center,
                      // Centreer de widgets horizontaal
                      children: <Widget>[
                        Row(
                          mainAxisAlignment: MainAxisAlignment.center,
                          // Centreer de widgets horizontaal
                          children: <Widget>[
                            ElevatedButton(
                              onPressed: () =>
                                  _addCommand("UPDATE_IRRIGATION_TIME,-5"),
                              child: Text('-5'),
                            ),
                            ElevatedButton(
                              onPressed: () =>
                                  _addCommand("UPDATE_IRRIGATION_TIME,-30"),
                              child: Text('-30'),
                            ),
                            SizedBox(width: 10),
                            // Voeg een beetje ruimte tussen de widgets

                            ElevatedButton(
                              onPressed: () =>
                                  _addCommand("UPDATE_IRRIGATION_TIME,30"),
                              child: Text('+30'),
                            ),
                            SizedBox(width: 10),
                            // Voeg een beetje ruimte tussen de widgets
                            ElevatedButton(
                              onPressed: () =>
                                  _addCommand("UPDATE_IRRIGATION_TIME,5"),
                              child: Text('+5'),
                            ),
                          ],
                        ),
                      ],
                    ),
                  ],
                ),
              ),

              // AREA

              Container(
                margin: EdgeInsets.all(10), // Marges rond het blok
                padding: EdgeInsets.all(15), // Binnenruimte binnen het blok
                decoration: buildBoxDecoration(),
                child: Column(
                  mainAxisSize: MainAxisSize.min, // Minimaliseer de hoogte
                  children: [
                    SizedBox(height: 10),
                    Text('Beregeningsgebied: ${beregeningData?.viewModel.valveState}'),
                    Row(
                      mainAxisAlignment: MainAxisAlignment.center,
                      children: <Widget>[
                        ElevatedButton(
                          onPressed: () => _addCommand("CHANGE_AREA,GAZON"),
                          child: Text('Gazon'),
                        ),
                        SizedBox(width: 10), // Ruimte tussen de knoppen
                        ElevatedButton(
                          onPressed: () => _addCommand("CHANGE_AREA,MOESTUIN"),
                          child: Text('Moestuin'),
                        ),
                      ],
                    ),
                  ],
                ),
              ),

// PLANNING
              Container(
                margin: EdgeInsets.all(10), // Marges rond het blok
                padding: EdgeInsets.all(15), // Binnenruimte binnen het blok
                decoration: buildBoxDecoration(),
                child: Column(
                  mainAxisSize: MainAxisSize.min, // Minimaliseer de hoogte
                  children: [
                    Text(
                        'Volgende planning: ${beregeningData?.viewModel.nextSchedule}'),
                    SizedBox(height: 10),
                    Row(
                      mainAxisAlignment: MainAxisAlignment.center,
                      // Centreer de widgets horizontaal
                      children: <Widget>[
                        ElevatedButton(
                          onPressed: () {
                            // Navigeren naar SecondPage
                            Navigator.push(
                              context,
                              MaterialPageRoute(
                                  builder: (context) =>
                                      Schedules(title: 'Planning')),
                            );
                          },
                          child: Text('Planning'),
                        ),
                      ],
                    ),
                  ],
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }

  BoxDecoration buildBoxDecoration() {
    return BoxDecoration(
      color: Colors.white.withOpacity(0.8),
      // Witte achtergrond met 50% transparantie
      borderRadius: BorderRadius.circular(15),
      // Afgeronde hoeken
      boxShadow: [
        BoxShadow(
          color: Colors.black.withOpacity(0.4), // Optionele schaduw
          blurRadius: 10,
          offset: Offset(0, 5),
        ),
      ],
    );
  }

  String calculateTimeDifference(
      int year, int month, int day, int hour, int minute, int second) {
    // Maak een DateTime object van de gegeven waarden
    final targetDateTime = DateTime(year, month, day, hour, minute, second);

    // Huidige tijd
    final now = DateTime.now();

    // Bereken het verschil
    final duration = targetDateTime.difference(now);

    // Controleer of de tijd al verstreken is
    if (duration.isNegative) {
      return "00:00:00"; // Als de tijd in het verleden ligt
    }

    // Haal uren, minuten en seconden uit het verschil
    final hours = duration.inHours;
    final minutes = duration.inMinutes % 60;
    final seconds = duration.inSeconds % 60;

    // Formatteer het resultaat naar hh:mm:ss
    return '${hours.toString().padLeft(2, '0')}:${minutes.toString().padLeft(2, '0')}:${seconds.toString().padLeft(2, '0')}';
  }
}

//---------
class LoginPage extends StatefulWidget {
  @override
  _LoginPageState createState() => _LoginPageState();
}

class _LoginPageState extends State<LoginPage> {
  final TextEditingController _emailController = TextEditingController();
  final TextEditingController _passwordController = TextEditingController();
  String _errorMessage = '';

  Future<void> _login() async {
    try {
      // Login met Firebase
      UserCredential userCredential =
          await FirebaseAuth.instance.signInWithEmailAndPassword(
        email: _emailController.text.trim(),
        password: _passwordController.text.trim(),
      );
      // Als login succesvol is, ga naar de volgende pagina
      Navigator.of(context).pushReplacement(
        MaterialPageRoute(
            builder: (context) =>
                MyHomePage(title: 'Robbert' 's tuinsproeiers')),
      );
    } catch (e) {
      // Toon foutbericht
      setState(() {
        _errorMessage = e.toString();
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text('Login'),
      ),
      body: Padding(
        padding: const EdgeInsets.all(16.0),
        child: Form(
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.stretch,
            children: [
              TextField(
                controller: _emailController,
                decoration: InputDecoration(
                  labelText: 'Gebruikersnaam',
                  border: OutlineInputBorder(),
                ),
                keyboardType: TextInputType.emailAddress,
                autofillHints: [
                  AutofillHints.username
                ], // Autofill voor gebruikersnaam
              ),
              SizedBox(height: 10), // Ruimte tussen velden
              TextField(
                controller: _passwordController,
                decoration: InputDecoration(
                  labelText: 'Wachtwoord',
                  border: OutlineInputBorder(),
                ),
                obscureText: true, // Maakt tekst verborgen
                autofillHints: [
                  AutofillHints.password
                ], // Autofill voor wachtwoord
              ),
              SizedBox(height: 10),
              ElevatedButton(
                onPressed: _login,
                child: Text('Login'),
              ),
              if (_errorMessage.isNotEmpty)
                Padding(
                  padding: const EdgeInsets.only(top: 10),
                  child: Text(
                    _errorMessage,
                    style: TextStyle(color: Colors.red),
                  ),
                ),
            ],
          ),
        ),
      ),
    );
  }
}

//--------
