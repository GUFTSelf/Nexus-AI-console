import SwiftUI

struct ContentView: View {
    @State private var input = """
    MODE: REPLAY_COMPARISON
    INPUT: 7
    OPERATIONS: MULTIPLY 8; SUBTRACT 11; DIVIDE 5; ADD 9
    REPLAY_COUNT: 2
    """
    @State private var finalValue = "—"
    @State private var disposition = "READY"
    @State private var traceHash = "—"
    @State private var canonicalRecord = "Submit a deterministic request to generate a canonical record."

    var body: some View {
        NavigationStack {
            ScrollView {
                VStack(alignment: .leading, spacing: 16) {
                    header

                    TextEditor(text: $input)
                        .font(.system(.body, design: .monospaced))
                        .frame(minHeight: 190)
                        .padding(8)
                        .scrollContentBackground(.hidden)
                        .background(Color.white.opacity(0.06), in: RoundedRectangle(cornerRadius: 12))
                        .overlay(RoundedRectangle(cornerRadius: 12).stroke(Color.green.opacity(0.5)))

                    Button(action: execute) {
                        Label("Execute and Replay", systemImage: "play.fill")
                            .frame(maxWidth: .infinity)
                            .fontWeight(.bold)
                    }
                    .buttonStyle(.borderedProminent)
                    .tint(.green)
                    .foregroundStyle(.black)

                    resultCard
                    canonicalCard
                }
                .padding()
            }
            .background(Color(red: 0.015, green: 0.025, blue: 0.02).ignoresSafeArea())
            .foregroundStyle(.white)
            .navigationTitle("Nexus AI")
            .navigationBarTitleDisplayMode(.inline)
        }
        .preferredColorScheme(.dark)
    }

    private var header: some View {
        VStack(alignment: .leading, spacing: 6) {
            Label("VEK Canonical Execution", systemImage: "checkmark.shield.fill")
                .foregroundStyle(.green)
                .font(.title2.bold())
            Text("Deterministic arithmetic, normalized replay, and SHA-256 integrity commitment.")
                .foregroundStyle(.secondary)
        }
    }

    private var resultCard: some View {
        VStack(alignment: .leading, spacing: 10) {
            HStack {
                Text(disposition).font(.headline.bold())
                Spacer()
                Text("FINAL: \(finalValue)").font(.headline.monospaced())
            }
            Text(traceHash)
                .font(.caption.monospaced())
                .textSelection(.enabled)
                .foregroundStyle(.green)
        }
        .padding()
        .background(Color.white.opacity(0.06), in: RoundedRectangle(cornerRadius: 12))
    }

    private var canonicalCard: some View {
        VStack(alignment: .leading, spacing: 8) {
            Text("CANONICAL RECORD").font(.caption.bold()).foregroundStyle(.green)
            Text(canonicalRecord)
                .font(.caption2.monospaced())
                .textSelection(.enabled)
        }
        .padding()
        .background(Color.white.opacity(0.04), in: RoundedRectangle(cornerRadius: 12))
    }

    private func execute() {
        do {
            let comparison = try CanonicalExecutionEngine.execute(input)
            finalValue = comparison.canonicalRecord.finalValue
            disposition = comparison.pass ? "PASS" : "FAIL"
            traceHash = comparison.canonicalRecord.sha256
            canonicalRecord = comparison.canonicalRecord.canonicalJSON
        } catch {
            finalValue = "—"
            disposition = "REJECT"
            traceHash = "—"
            canonicalRecord = error.localizedDescription
        }
    }
}

#Preview {
    ContentView()
}
